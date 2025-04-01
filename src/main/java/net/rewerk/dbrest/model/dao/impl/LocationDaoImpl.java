package net.rewerk.dbrest.model.dao.impl;

import net.rewerk.dbrest.helper.ConnectionManager;
import net.rewerk.dbrest.model.dao.LocationDao;
import net.rewerk.dbrest.model.entity.Location;
import net.rewerk.dbrest.util.DBUtil;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class LocationDaoImpl implements LocationDao {
    @Override
    public Location save(Location location) {
        if (location == null) return null;
        Location result;
        if (location.getId() == null) {
            result = create(location);
        } else {
            result = existsById(location.getId()) ?
                    update(location) : create(location);
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
                        delete from locations where id = ?
                    """);
            ps.setLong(1, id);
            int count = ps.executeUpdate();
            result = count > 0;
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[LocationDaoImpl] delete() SQLException: " + e.getMessage());
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
                        select * from locations where id = ?
                    """);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            result = rs.next();
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[LocationDaoImpl] existsById() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public List<Location> findAll() {
        List<Location> result = new ArrayList<>();
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select * from locations
                    """);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rsToEntity(rs));
            }
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[LocationDaoImpl] findAll() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public Location getById(Long id) {
        Location result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select * from locations where id = ?
                    """);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = rsToEntity(rs);
            }
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[LocationDaoImpl] getById() SQLException: " + e.getMessage());
        }
        return result;
    }

    private Location create(Location location) {
        Location result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        insert into locations(address, country) values(?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            populateStatement(ps, location);
            int inserted = ps.executeUpdate();
            Long id = DBUtil.getIdAfterInsert(ps, inserted);
            if (id != null) {
                location.setId(id);
                result = location;
            }
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[LocationDaoImpl] create() SQLException: " + e.getMessage());
        }
        return result;
    }

    private Location update(Location location) {
        Location result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        update locations set address = ?, country = ?, updatedAt = ? where id = ?
                    """);
            populateStatement(ps, location);
            ps.setTimestamp(3, Timestamp.from(Instant.now()));
            ps.setLong(4, location.getId());
            int count = ps.executeUpdate();
            result = count > 0 ? location : null;
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[LocationDaoImpl] update() SQLException: " + e.getMessage());
        }
        return result;
    }

    private Location rsToEntity(ResultSet rs) {
        try {
            return Location.builder()
                    .id(rs.getLong("id"))
                    .address(rs.getString("address"))
                    .country(rs.getString("country"))
                    .createdAt(rs.getString("createdAt"))
                    .updatedAt(rs.getString("updatedAt"))
                    .build();
        } catch (SQLException e) {
            System.out.println("[LocationDaoImpl] rsToEntity() SQLException: " + e.getMessage());
            return null;
        }
    }

    private void populateStatement(PreparedStatement ps, Location location) {
        try {
            ps.setString(1, location.getAddress());
            ps.setString(2, location.getCountry());
        } catch (SQLException e) {
            System.out.println("[LocationDaoImpl] populateStatement() SQLException: " + e.getMessage());
        }
    }
}
