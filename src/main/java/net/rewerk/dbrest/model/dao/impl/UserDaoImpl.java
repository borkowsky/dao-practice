package net.rewerk.dbrest.model.dao.impl;

import net.rewerk.dbrest.enumerator.UserGender;
import net.rewerk.dbrest.helper.ConnectionManager;
import net.rewerk.dbrest.model.dao.UserDao;
import net.rewerk.dbrest.model.entity.User;
import net.rewerk.dbrest.service.PasswordService;
import net.rewerk.dbrest.util.DBUtil;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {
    @Override
    public User save(User user) {
        if (user == null) return null;
        User result;
        if (user.getId() == null) {
            result = create(user);
        } else {
            User currentUser = getById(user.getId());
            if (currentUser == null) {
                result = create(user);
            } else {
                result = update(user);
            }
        }
        return result;
    }

    private User create(User user) {
        User result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        insert into users (username,
                                           password,
                                           name,
                                           age,
                                           gender,
                                           passport,
                                           email
                                           ) values(?, ?, ?, ?, cast(? as users_gender), ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            populatePSData(ps, user);
            int inserted = ps.executeUpdate();
            Long id = DBUtil.getIdAfterInsert(ps, inserted);
            if (id != null) {
                user.setId(id);
                result = user;
            }
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[UserDaoImpl] create SQLException: " + e.getMessage());
        }
        return result;
    }

    private User update(User user) {
        User result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        update users set username = ?,
                                         password = ?,
                                         name = ?,
                                         age = ?,
                                         gender = cast(? as users_gender),
                                         passport = ?,
                                         email = ?,
                                         updatedAt = ?
                                     where id = ?
                    """);
            populatePSData(ps, user);
            ps.setTimestamp(8, Timestamp.from(Instant.now()));
            ps.setLong(9, user.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = entityFromResultSet(rs);
            }
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[UserDaoImpl] update SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public boolean delete(User user) {
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        delete from users where id = ?
                    """);
            ps.setLong(1, user.getId());
            int rows = ps.executeUpdate();
            DBUtil.closeResources(ps, conn);
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("[UserDaoImpl] delete() SQLException: " + e.getMessage());
            return false;
        }
    }

    @Override
    public User getById(Long id) {
        User result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select * from users where id = ?
                    """);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = entityFromResultSet(rs);
            }
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[UserDaoImpl] getById() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public boolean existsById(Long id) {
        boolean result = false;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select * from users where id = ?
                    """);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            result = rs.next();
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[UserDaoImpl] existsById() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select * from users
                    """);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(entityFromResultSet(rs));
            }
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[UserDaoImpl] findAll() SQLException: " + e.getMessage());
        }
        return users;
    }

    @Override
    public List<User> findByUsername(String username) {
        List<User> users = new ArrayList<>();
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select * from users where username = ?
                    """);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(entityFromResultSet(rs));
            }
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[UserDaoImpl] findByUsername() SQLException: " + e.getMessage());
        }
        return users;
    }

    private User entityFromResultSet(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .username(rs.getString("username"))
                .password(rs.getString("password"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .passport(rs.getString("passport"))
                .age(rs.getInt("age"))
                .gender(UserGender.valueOf(rs.getString("gender").toUpperCase()))
                .createdAt(rs.getString("createdAt"))
                .updatedAt(rs.getString("updatedAt"))
                .build();
    }

    private void populatePSData(PreparedStatement ps, User user) throws SQLException {
        String password = user.getPassword();
        ps.setString(1, user.getUsername());
        ps.setString(2, password == null ? null : PasswordService.encryptPassword(password));
        ps.setString(3, user.getName());
        if (user.getAge() != null) {
            ps.setInt(4, user.getAge());
        } else {
            ps.setNull(4, Types.INTEGER);
        }
        ps.setString(5, user.getGender().toString().toLowerCase());
        if (user.getPassport() != null) ps.setString(6, user.getPassport());
        if (user.getEmail() != null) ps.setString(7, user.getEmail());
    }
}
