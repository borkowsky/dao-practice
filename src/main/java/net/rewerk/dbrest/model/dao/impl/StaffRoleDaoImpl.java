package net.rewerk.dbrest.model.dao.impl;

import net.rewerk.dbrest.helper.ConnectionManager;
import net.rewerk.dbrest.model.dao.StaffRoleDao;
import net.rewerk.dbrest.model.entity.StaffRole;
import net.rewerk.dbrest.util.DBUtil;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class StaffRoleDaoImpl implements StaffRoleDao {
    @Override
    public StaffRole save(StaffRole staffRole) {
        if (staffRole == null) return null;
        StaffRole result;
        if (staffRole.getId() == null) {
            result = create(staffRole);
        } else {
            result = getById(staffRole.getId()) == null ?
                    create(staffRole) :
                    update(staffRole);
        }
        return result;
    }

    @Override
    public boolean delete(Long id) {
        if (id == null) return false;
        boolean result = false;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        delete from staff_roles where id = ?
                    """);
            ps.setLong(1, id);
            int count = ps.executeUpdate();
            result = count > 0;
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[StaffRoleDaoImpl] delete() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;
        boolean result = false;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select * from staff_roles where id = ?
                    """);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            result = rs.next();
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[StaffRoleDaoImpl] existsById() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public List<StaffRole> findAll() {
        List<StaffRole> result = new ArrayList<>();
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select * from staff_roles
                    """);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rsToEntity(rs));
            }
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[StaffRoleDaoImpl] findAll() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public StaffRole getById(Long id) {
        StaffRole result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select * from staff_roles where id = ?
                    """);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = rsToEntity(rs);
            }
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[StaffRoleDaoImpl] getById() SQLException: " + e.getMessage());
        }
        return result;
    }

    private StaffRole create(StaffRole staffRole) {
        StaffRole result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        insert into staff_roles(name) values(?)
                    """, Statement.RETURN_GENERATED_KEYS);
            populateStatement(ps, staffRole);
            int inserted = ps.executeUpdate();
            Long id = DBUtil.getIdAfterInsert(ps, inserted);
            if (id != null) {
                staffRole.setId(id);
                result = staffRole;
            }
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[StaffRoleDaoImpl] create() SQLException: " + e.getMessage());
        }
        return result;
    }

    private StaffRole update(StaffRole staffRole) {
        StaffRole result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        update staff_roles set name = ?, updatedAt = ? where id = ?
                    """);
            populateStatement(ps, staffRole);
            ps.setTimestamp(2, Timestamp.from(Instant.now()));
            ps.setLong(3, staffRole.getId());
            int count = ps.executeUpdate();
            result = count > 0 ? staffRole : null;
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[StaffRoleDaoImpl] update() SQLException: " + e.getMessage());
        }
        return result;
    }

    private StaffRole rsToEntity(ResultSet rs) {
        try {
            return StaffRole.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .createdAt(rs.getString("createdAt"))
                    .updatedAt(rs.getString("updatedAt"))
                    .build();
        } catch (SQLException e) {
            System.out.println("[StaffRoleDaoImpl] rsToEntity() SQLException: " + e.getMessage());
            return null;
        }
    }

    private void populateStatement(PreparedStatement ps, StaffRole staffRole) {
        try {
            ps.setString(1, staffRole.getName());
        } catch (SQLException e) {
            System.out.println("[StaffRoleDaoImpl] populateStatement() SQLException: " + e.getMessage());
        }
    }
}
