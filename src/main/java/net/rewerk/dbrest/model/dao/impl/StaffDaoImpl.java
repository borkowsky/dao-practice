package net.rewerk.dbrest.model.dao.impl;

import net.rewerk.dbrest.helper.ConnectionManager;
import net.rewerk.dbrest.model.dao.StaffDao;
import net.rewerk.dbrest.model.entity.Staff;
import net.rewerk.dbrest.model.entity.StaffRole;
import net.rewerk.dbrest.util.DBUtil;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaffDaoImpl implements StaffDao {
    @Override
    public Staff save(Staff Staff) {
        if (Staff == null) return null;
        Staff result;
        if (Staff.getId() == null) {
            result = create(Staff);
        } else {
            result = getById(Staff.getId()) == null ?
                    create(Staff) :
                    update(Staff);
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
                        delete from staff where id = ?
                    """);
            ps.setLong(1, id);
            int count = ps.executeUpdate();
            result = count > 0;
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[StaffDaoImpl] delete() SQLException: " + e.getMessage());
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
                        select * from staff where id = ?
                    """);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            result = rs.next();
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[StaffDaoImpl] existsById() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public List<Staff> findAll() {
        List<Staff> result = new ArrayList<>();
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select *,
                            sr.id as role_id,
                            sr.name as role_name
                            from staff s
                            left join staff_roles sr on s.roleId = sr.id
                    """);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rsToEntity(rs));
            }
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[StaffDaoImpl] findAll() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public Staff getById(Long id) {
        Staff result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select *,
                            sr.id as role_id,
                            sr.name as role_name
                            from staff s
                            left join staff_roles sr on s.roleId = sr.id
                            where s.id = ?
                    """);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = rsToEntity(rs);
            }
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[StaffDaoImpl] getById() SQLException: " + e.getMessage());
        }
        return result;
    }

    @Override
    public List<Staff> findByIds(List<Long> ids) {
        if (ids == null) return List.of();
        List<Staff> result = new ArrayList<>();
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        select *,
                                sr.id as role_id,
                                sr.name as role_name
                                from staff s
                                left join staff_roles sr on s.roleId = sr.id
                                where s.id in (select unnest(?))
                    """);
            ps.setArray(1, conn.createArrayOf("long", ids.toArray()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rsToEntity(rs));
            }
            DBUtil.closeResources(rs, ps, conn);
        } catch (SQLException e) {
            System.out.println("[StaffDaoImpl] findByIds() SQLException: " + e.getMessage());
        }
        return result;
    }

    private Staff create(Staff staff) {
        Staff result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        insert into staff(name, roleId) values(?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            populateStatement(ps, staff);
            int inserted = ps.executeUpdate();
            Long id = DBUtil.getIdAfterInsert(ps, inserted);
            if (id != null) {
                staff.setId(id);
                result = staff;
            }
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[StaffDaoImpl] create() SQLException: " + e.getMessage());
        }
        return result;
    }

    private Staff update(Staff Staff) {
        Staff result = null;
        try {
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                        update staff set name = ?, roleId = ?, updatedAt = ? where id = ?
                    """);
            populateStatement(ps, Staff);
            ps.setTimestamp(3, Timestamp.from(Instant.now()));
            ps.setLong(4, Staff.getId());
            int count = ps.executeUpdate();
            result = count > 0 ? Staff : null;
            DBUtil.closeResources(ps, conn);
        } catch (SQLException e) {
            System.out.println("[StaffDaoImpl] update() SQLException: " + e.getMessage());
        }
        return result;
    }

    private Staff rsToEntity(ResultSet rs) {
        try {
            return Staff.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .role(StaffRole.builder()
                            .id(rs.getLong("role_id"))
                            .name(rs.getString("role_name"))
                            .build())
                    .createdAt(rs.getString("createdAt"))
                    .updatedAt(rs.getString("updatedAt"))
                    .build();
        } catch (SQLException e) {
            System.out.println("[StaffDaoImpl] rsToEntity() SQLException: " + e.getMessage());
            return null;
        }
    }

    private void populateStatement(PreparedStatement ps, Staff Staff) {
        try {
            ps.setString(1, Staff.getName());
            ps.setLong(2, Staff.getRole().getId());
        } catch (SQLException e) {
            System.out.println("[StaffDaoImpl] populateStatement() SQLException: " + e.getMessage());
        }
    }
}
