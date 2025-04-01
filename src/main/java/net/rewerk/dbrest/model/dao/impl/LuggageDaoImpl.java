package net.rewerk.dbrest.model.dao.impl;

import net.rewerk.dbrest.helper.ConnectionManager;
import net.rewerk.dbrest.model.dao.LuggageDao;
import net.rewerk.dbrest.model.entity.Luggage;
import net.rewerk.dbrest.util.DBUtil;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class LuggageDaoImpl implements LuggageDao {
    @Override
    public Luggage save(Luggage luggage) {
        if (luggage == null) return null;
        Luggage result;
        if (luggage.getId() == null) {
            result = create(luggage);
        } else {
            result = getById(luggage.getId()) == null ?
                    create(luggage) :
                    update(luggage);
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
                        delete from luggage where id = ?
                    """);
            ps.setLong(1, id);
            int count = ps.executeUpdate();
            result = count > 0;
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[LuggageDaoImplImpl] delete() SQLException: " + e.getMessage());
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
                        select * from luggage where id = ?
                    """);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            result = rs.next();
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[LuggageDaoImplImpl] existsById() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public List<Luggage> findAll() {
        List<Luggage> result = new ArrayList<>();
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select * from luggage
                    """);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rsToEntity(rs));
            }
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[LuggageDaoImplImpl] findAll() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public Luggage getById(Long id) {
        Luggage result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select * from luggage where id = ?
                    """);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = rsToEntity(rs);
            }
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[LuggageDaoImplImpl] getById() SQLException: " + e.getMessage());
        }
        return result;
    }

    private Luggage create(Luggage luggage) {
        Luggage result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        insert into luggage(weight, userId) values(?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            populateStatement(ps, luggage);
            int inserted = ps.executeUpdate();
            Long id = DBUtil.getIdAfterInsert(ps, inserted);
            if (id != null) {
                luggage.setId(id);
                result = luggage;
            }
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[LuggageDaoImplImpl] create() SQLException: " + e.getMessage());
        }
        return result;
    }

    private Luggage update(Luggage luggage) {
        Luggage result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        update luggage set weight = ?, userId = ?, updatedAt = ? where id = ?
                    """);
            populateStatement(ps, luggage);
            ps.setTimestamp(3, Timestamp.from(Instant.now()));
            ps.setLong(4, luggage.getId());
            int count = ps.executeUpdate();
            result = count > 0 ? luggage : null;
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[LuggageDaoImplImpl] update() SQLException: " + e.getMessage());
        }
        return result;
    }

    private Luggage rsToEntity(ResultSet rs) {
        try {
            return Luggage.builder()
                    .id(rs.getLong("id"))
                    .weight(rs.getInt("weight"))
                    .userId(rs.getLong("userId"))
                    .createdAt(rs.getString("createdAt"))
                    .updatedAt(rs.getString("updatedAt"))
                    .build();
        } catch (SQLException e) {
            System.out.println("[LuggageDaoImplImpl] rsToEntity() SQLException: " + e.getMessage());
            return null;
        }
    }

    private void populateStatement(PreparedStatement ps, Luggage luggage) {
        try {
            ps.setInt(1, luggage.getWeight());
            ps.setLong(2, luggage.getUserId());
        } catch (SQLException e) {
            System.out.println("[LuggageDaoImplImpl] populateStatement() SQLException: " + e.getMessage());
        }
    }
}
