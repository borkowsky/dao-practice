package net.rewerk.dbrest.model.dao.impl;

import net.rewerk.dbrest.helper.ConnectionManager;
import net.rewerk.dbrest.model.dao.AirplaneDao;
import net.rewerk.dbrest.model.entity.Airplane;
import net.rewerk.dbrest.model.entity.Staff;
import net.rewerk.dbrest.model.entity.StaffRole;
import net.rewerk.dbrest.util.CommonUtil;
import net.rewerk.dbrest.util.DBUtil;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class AirplaneDaoImpl implements AirplaneDao {
    @Override
    public Airplane save(Airplane airplane) {
        if (airplane == null) return null;
        Airplane result;
        if (airplane.getId() == null) {
            result = create(airplane);
        } else {
            result = existsById(airplane.getId()) ?
                    update(airplane) : create(airplane);
        }
        return result;
    }

    @Override
    public boolean delete(Long id) {
        if (id == null) return false;
        Airplane airplane = getById(id);
        if (airplane == null) return false;
        boolean result = false;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            try {
                PreparedStatement ps = conn.prepareStatement("""
                            delete from airplane_staff where airplaneId = ?
                        """);
                ps.setLong(1, id);
                ps.executeUpdate();
                PreparedStatement ps1 = conn.prepareStatement("""
                            delete from airplanes where id = ?
                        """);
                ps1.setLong(1, id);
                ps1.executeUpdate();
                conn.commit();
                ps.close();
                ps1.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println("[AirplaneDaoImpl] delete() SQLException in transaction, do rollback: " + e.getMessage());
                conn.rollback();
            }
            result = true;
        } catch (SQLException e) {
            System.out.println("[AirplaneDaoImpl] delete() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public Airplane getById(Long id) {
        if (id == null) return null;
        Airplane result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                    select a.*,
                    	a_s.airplaneId as a_s_airplaneId,
                    	a_s.staffId as a_s_staffId,
                    	s.id as staff_id,
                    	s.name as staff_name,
                    	sr.id as role_id,
                    	sr.name as role_name
                    	from airplanes a
                    	left outer join airplane_staff as a_s on a.id = a_s.airplaneId
                    	left outer join staff as s on a_s.staffId = s.id
                    	left outer join staff_roles sr on s.roleId = sr.id
                    	where a.id = ?;
                    """);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            result = rsToEntityList(rs).stream()
                    .filter(i -> i.getId().equals(id))
                    .findFirst().orElse(null);
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[AirplaneDaoImpl] getById() SQLException: " + e.getMessage());
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
                        select * from airplanes where id = ?
                    """);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            result = rs.next();
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[AirplaneDaoImpl] existsById() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public List<Airplane> findAll() {
        List<Airplane> result = new ArrayList<>();
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                    select a.*,
                    	a_s.airplaneId as a_s_airplaneId,
                    	a_s.staffId as a_s_staffId,
                    	s.id as staff_id,
                    	s.name as staff_name,
                    	sr.id as role_id,
                    	sr.name as role_name
                    	from airplanes a
                    	left outer join airplane_staff as a_s on a.id = a_s.airplaneId
                    	left outer join staff as s on a_s.staffId = s.id
                    	left outer join staff_roles sr on s.roleId = sr.id
                    """);
            ResultSet rs = ps.executeQuery();
            result = rsToEntityList(rs);
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[AirplaneDaoImpl] findAll() SQLException: " + e.getMessage());
        }
        return result;
    }

    private Airplane create(Airplane airplane) {
        Airplane result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        insert into airplanes(name, number) values(?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            populateStatement(ps, airplane);
            int inserted = ps.executeUpdate();
            Long id = DBUtil.getIdAfterInsert(ps, inserted);
            if (id != null) {
                airplane.setId(id);
                defineRelations(airplane);
                result = airplane;
            }
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[AirplaneDaoImpl] create() SQLException: " + e.getMessage());
        }
        return result;
    }

    private Airplane update(Airplane airplane) {
        Airplane result = null;
        try {
            Airplane old = getById(airplane.getId());
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        update airplanes set name = ?, number = ?, updatedAt = ? where id = ?
                    """);
            populateStatement(ps, airplane);
            ps.setTimestamp(3, Timestamp.from(Instant.now()));
            ps.setLong(4, airplane.getId());
            int count = ps.executeUpdate();
            if (count > 0) {
                if (!CommonUtil.isListEquals(
                        old.getStaff().stream().map(Staff::getId).collect(Collectors.toList()),
                        airplane.getStaff().stream().map(Staff::getId).collect(Collectors.toList())
                )) {
                    dropRelations(old);
                    defineRelations(airplane);
                }
            }
            result = count > 0 ? airplane : null;
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[AirplaneDaoImpl] update() SQLException: " + e.getMessage());
        }
        return result;
    }

    private List<Airplane> rsToEntityList(ResultSet rs) {
        Map<String, Airplane> map = new HashMap<>();
        try {
            while (rs.next()) {
                String key = String.format("airplane-%d", rs.getLong("id"));
                if (map.containsKey(key)) {
                    Airplane airplane = map.get(key);
                    List<Staff> staffs = airplane.getStaff();
                    staffs.add(Staff.builder()
                            .id(rs.getLong("staff_id"))
                            .name(rs.getString("staff_name"))
                            .role(StaffRole.builder()
                                    .id(rs.getLong("role_id"))
                                    .name(rs.getString("role_name"))
                                    .build())
                            .build());
                    airplane.setStaff(staffs);
                } else {
                    List<Staff> staffs = new ArrayList<>();
                    staffs.add(Staff.builder()
                            .id(rs.getLong("staff_id"))
                            .name(rs.getString("staff_name"))
                            .role(StaffRole.builder()
                                    .id(rs.getLong("role_id"))
                                    .name(rs.getString("role_name"))
                                    .build())
                            .build());
                    map.put(key, Airplane.builder()
                            .id(rs.getLong("id"))
                            .name(rs.getString("name"))
                            .number(rs.getString("number"))
                            .staff(staffs)
                            .createdAt(rs.getString("createdAt"))
                            .updatedAt(rs.getString("updatedAt"))
                            .build());
                }
            }
        } catch (SQLException e) {
            System.out.println("[AirplaneDaoImpl] rsToEntity() SQLException: " + e.getMessage());
        }
        return new ArrayList<>(map.values());
    }

    private void populateStatement(PreparedStatement ps, Airplane airplane) {
        try {
            ps.setString(1, airplane.getName());
            ps.setString(2, airplane.getNumber());
        } catch (SQLException e) {
            System.out.println("[AirplaneDaoImpl] populateStatement() SQLException: " + e.getMessage());
        }
    }

    private void dropRelations(Airplane airplane) {
        try {
            for (Staff staff : airplane.getStaff()) {
                Connection conn = ConnectionManager.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement("""
                            delete from airplane_staff where airplaneId = ? and staffId = ?
                        """);
                ps.setLong(1, airplane.getId());
                ps.setLong(2, staff.getId());
                ps.executeUpdate();
                ps.close();
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("[AirplaneDaoImpl] dropRelations() SQLException: " + e.getMessage());
        }
    }

    private void defineRelations(Airplane airplane) {
        try {
            for (Staff staff : airplane.getStaff()) {
                Connection conn = ConnectionManager.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement("""
                            select * from airplane_staff where airplaneId = ? and staffId = ?
                        """);
                ps.setLong(1, airplane.getId());
                ps.setLong(2, staff.getId());
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    PreparedStatement psr = conn.prepareStatement("""
                               insert into airplane_staff (airplaneId, staffId) values (?, ?)
                            """);
                    psr.setLong(1, airplane.getId());
                    psr.setLong(2, staff.getId());
                    psr.executeUpdate();
                    psr.close();
                }
                rs.close();
                ps.close();
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("[AirplaneDaoImpl] defineRelations() SQLException: " + e.getMessage());
        }
    }
}
