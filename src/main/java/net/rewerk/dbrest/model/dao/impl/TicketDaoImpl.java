package net.rewerk.dbrest.model.dao.impl;

import net.rewerk.dbrest.enumerator.TicketClass;
import net.rewerk.dbrest.helper.ConnectionManager;
import net.rewerk.dbrest.model.dao.*;
import net.rewerk.dbrest.model.dto.UserDto;
import net.rewerk.dbrest.model.dto.mapper.UserMapper;
import net.rewerk.dbrest.model.entity.*;
import net.rewerk.dbrest.util.DBUtil;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TicketDaoImpl implements TicketDao {
    @Override
    public Ticket save(Ticket ticket) {
        if (ticket == null) return null;
        Ticket result;
        if (ticket.getId() == null) {
            result = create(ticket);
        } else {
            result = getById(ticket.getId()) == null ?
                    create(ticket) :
                    update(ticket);
        }
        return result;
    }

    @Override
    public boolean delete(Long id) {
        if (id == null) return false;
        boolean result = false;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            Ticket ticket = getById(id);
            PreparedStatement ps = null;
            PreparedStatement ps1 = null;
            try {
                ps = conn.prepareStatement("""
                        delete from tickets where id = ?
                    """);
                ps.setLong(1, id);
                int count = ps.executeUpdate();
                if (ticket != null && ticket.getLuggage() != null && ticket.getLuggage().getId() != null) {
                    ps1 = conn.prepareStatement("""
                        delete from luggage where id = ?
                    """);
                    ps1.setLong(1, ticket.getLuggage().getId());
                    ps1.executeUpdate();
                }
                conn.commit();
                result = count > 0;
            } catch (SQLException e) {
                conn.rollback();
            }
            conn.setAutoCommit(true);
            if (ps != null) ps.close();
            if (ps1 != null) ps1.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("[TicketDaoImpl] delete() SQLException: " + e.getMessage());
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
                        select * from tickets where id = ?
                    """);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            result = rs.next();
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[TicketDaoImpl] existsById() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public List<Ticket> findAll() {
        List<Ticket> result = new ArrayList<>();
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select * from tickets
                    """);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rsToEntity(rs));
            }
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[TicketDaoImpl] findAll() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public List<Ticket> findByUserId(Long userId) {
        List<Ticket> result = new ArrayList<>();
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select * from tickets where userId = ?
                    """);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rsToEntity(rs));
            }
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[TicketDaoImpl] findByUserId() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public Ticket getById(Long id) {
        Ticket result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select * from tickets where id = ?
                    """);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = rsToEntity(rs);
            }
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[TicketDaoImpl] getById() SQLException: " + e.getMessage());
        }
        return result;
    }

    private Ticket create(Ticket ticket) {
        Ticket result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        insert into tickets(passport,
                                            class,
                                            routeId,
                                            userId,
                                            staffId,
                                            luggageId)
                        values(?, cast(? as tickets_class), ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            populateStatement(ps, ticket);
            int inserted = ps.executeUpdate();
            Long id = DBUtil.getIdAfterInsert(ps, inserted);
            if (id != null) {
                ticket.setId(id);
                result = ticket;
            }
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[TicketDaoImpl] create() SQLException: " + e.getMessage());
        }
        return result;
    }

    private Ticket update(Ticket ticket) {
        Ticket result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        update tickets set passport = ?,
                                           class = ?,
                                           routeId = ?,
                                           userId = ?,
                                           staffId = ?,
                                           luggageId = ?,
                                           updatedAt = ?
                                       where id = ?
                    """);
            populateStatement(ps, ticket);
            ps.setTimestamp(7, Timestamp.from(Instant.now()));
            ps.setLong(8, ticket.getId());
            int count = ps.executeUpdate();
            result = count > 0 ? ticket : null;
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[TicketDaoImpl] update() SQLException: " + e.getMessage());
        }
        return result;
    }

    private Ticket rsToEntity(ResultSet rs) {
        try {
            UserDao userDao = new UserDaoImpl();
            RouteDao routeDao = new RouteDaoImpl();
            StaffDao staffDao = new StaffDaoImpl();
            Ticket result = Ticket.builder()
                    .id(rs.getLong("id"))
                    .passport(rs.getString("passport"))
                    .user(UserDto.builder()
                            .id(rs.getLong("userId"))
                            .build())
                    .ticketClass(TicketClass.valueOf(rs.getString("class").toUpperCase()))
                    .route(Route.builder()
                            .id(rs.getLong("routeId"))
                            .build())
                    .staff(Staff.builder()
                            .id(rs.getLong("staffId"))
                            .build())
                    .createdAt(rs.getString("createdAt"))
                    .updatedAt(rs.getString("updatedAt"))
                    .build();
            result.setUser(UserMapper.toDto(userDao.getById(result.getUser().getId())));
            result.setRoute(routeDao.getById(result.getRoute().getId()));
            result.setStaff(staffDao.getById(result.getStaff().getId()));
            if (rs.getLong("luggageId") > 0) {
                LuggageDao luggageDao = new LuggageDaoImpl();
                result.setLuggage(luggageDao.getById(rs.getLong("luggageId")));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("[TicketDaoImpl] rsToEntity() SQLException: " + e.getMessage());
            return null;
        }
    }

    private void populateStatement(PreparedStatement ps, Ticket ticket) {
        try {
            ps.setString(1, ticket.getPassport());
            ps.setString(2, ticket.getTicketClass().toString().toLowerCase());
            ps.setLong(3, ticket.getRoute().getId());
            ps.setLong(4, ticket.getUser().getId());
            ps.setLong(5, ticket.getStaff().getId());
            if (ticket.getLuggage() != null) {
                ps.setLong(6, ticket.getLuggage().getId());
            } else {
                ps.setNull(6, Types.NULL);
            }
        } catch (SQLException e) {
            System.out.println("[TicketDaoImpl] populateStatement() SQLException: " + e.getMessage());
        }
    }
}
