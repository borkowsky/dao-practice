package net.rewerk.dbrest.model.dao.impl;

import net.rewerk.dbrest.helper.ConnectionManager;
import net.rewerk.dbrest.model.dao.AirplaneDao;
import net.rewerk.dbrest.model.dao.RouteDao;
import net.rewerk.dbrest.model.entity.Airplane;
import net.rewerk.dbrest.model.entity.Location;
import net.rewerk.dbrest.model.entity.Route;
import net.rewerk.dbrest.util.DBUtil;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RouteDaoImpl implements RouteDao {
    @Override
    public Route save(Route route) {
        if (route == null) return null;
        Route result;
        if (route.getId() == null) {
            result = create(route);
        } else {
            result = getById(route.getId()) == null ?
                    create(route) : update(route);
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
                        delete from routes where id = ?
                    """);
            ps.setLong(1, id);
            int count = ps.executeUpdate();
            result = count > 0;
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[RouteDaoImpl] delete() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public Route getById(Long id) {
        if (id == null) return null;
        Route result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select r.*,
                            dl.id as dl_id,
                            dl.address as dl_address,
                            dl.country as dl_country,
                            al.id as al_id,
                            al.address as al_address,
                            al.country as al_country
                            from routes r
                            left join locations dl on r.departureLocationId = dl.id
                            left join locations al on r.arrivalLocationId = al.id
                            where r.id = ?
                    """);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            result = rsToEntityList(rs).stream()
                    .filter(i -> i.getId().equals(id))
                    .findFirst().orElse(null);
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[RouteDaoImpl] getById() SQLException: " + e.getMessage());
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
                        select * from routes where id = ?
                    """);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            result = rs.next();
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[RouteDaoImpl] existsById() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public List<Route> findAll() {
        List<Route> result = new ArrayList<>();
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select r.*,
                            dl.id as dl_id,
                            dl.address as dl_address,
                            dl.country as dl_country,
                            al.id as al_id,
                            al.address as al_address,
                            al.country as al_country
                            from routes r
                            left join locations dl on r.departureLocationId = dl.id
                            left join locations al on r.arrivalLocationId = al.id
                    """);
            ResultSet rs = ps.executeQuery();
            result = rsToEntityList(rs);
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[RouteDaoImpl] findAll() SQLException: " + e.getMessage());
        }
        return result;
    }

    private Route create(Route route) {
        Route result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        insert into routes(airplaneId,
                                           departureTime,
                                           arrivalTime,
                                           departureLocationId,
                                           arrivalLocationId
                                           ) values(?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            populateStatement(ps, route);
            int inserted = ps.executeUpdate();
            Long id = DBUtil.getIdAfterInsert(ps, inserted);
            if (id != null) {
                route.setId(id);
                result = route;
            }
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[RouteDaoImpl] create() SQLException: " + e.getMessage());
        }
        return result;
    }

    private Route update(Route route) {
        Route result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        update routes
                            set airplaneId = ?,
                            departureTime = ?,
                            arrivalTime = ?,
                            departureLocationId = ?,
                            arrivalLocationId = ?,
                            updatedAt = ?
                            where id = ?
                    """);
            populateStatement(ps, route);
            ps.setTimestamp(6, Timestamp.from(Instant.now()));
            ps.setLong(7, route.getId());
            int count = ps.executeUpdate();
            result = count > 0 ? route : null;
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[RouteDaoImpl] update() SQLException: " + e.getMessage());
        }
        return result;
    }

    private List<Route> rsToEntityList(ResultSet rs) {
        List<Route> result = new ArrayList<>();
        try {
            while (rs.next()) {
                result.add(Route.builder()
                        .id(rs.getLong("id"))
                        .departureTime(rs.getString("departureTime"))
                        .arrivalTime(rs.getString("arrivalTime"))
                        .airplane(Airplane.builder()
                                .id(rs.getLong("airplaneId"))
                                .build())
                        .departureLocation(Location.builder()
                                .id(rs.getLong("dl_id"))
                                .address(rs.getString("dl_address"))
                                .country(rs.getString("dl_country"))
                                .build())
                        .arrivalLocation(Location.builder()
                                .id(rs.getLong("al_id"))
                                .address(rs.getString("al_address"))
                                .country(rs.getString("al_country"))
                                .build())
                        .createdAt(rs.getString("createdAt"))
                        .updatedAt(rs.getString("updatedAt"))
                        .build());
            }
        } catch (SQLException e) {
            System.out.println("[RouteDaoImpl] rsToEntity() SQLException: " + e.getMessage());
        }
        AirplaneDao airplaneDao = new AirplaneDaoImpl();
        result = result.stream()
                .peek(i -> {
                    Airplane airplane = airplaneDao.getById(i.getAirplane().getId());
                    if (airplane != null) i.setAirplane(airplane);
                })
                .collect(Collectors.toList());
        return result;
    }

    private void populateStatement(PreparedStatement ps, Route route) {
        try {
            if (route.getAirplane() != null) ps.setLong(1, route.getAirplane().getId());
            ps.setString(2, route.getDepartureTime());
            ps.setString(3, route.getArrivalTime());
            if (route.getDepartureLocation() != null) ps.setLong(4, route.getDepartureLocation().getId());
            if (route.getArrivalLocation() != null) ps.setLong(5, route.getArrivalLocation().getId());
        } catch (SQLException e) {
            System.out.println("[RouteDaoImpl] populateStatement() SQLException: " + e.getMessage());
        }
    }
}
