package net.rewerk.dbrest.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.rewerk.dbrest.model.dao.StaffDao;
import net.rewerk.dbrest.model.dao.StaffRoleDao;
import net.rewerk.dbrest.model.dao.impl.StaffDaoImpl;
import net.rewerk.dbrest.model.dao.impl.StaffRoleDaoImpl;
import net.rewerk.dbrest.model.entity.Staff;
import net.rewerk.dbrest.model.entity.StaffRole;
import net.rewerk.dbrest.util.ServletUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/staff")
public class StaffController extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StaffDao staffDao = new StaffDaoImpl();
        List<Staff> staffs = staffDao.findAll();
        ServletUtil.sendPayloadResponse(resp,
                List.of(),
                staffs,
                new int[]{
                        HttpServletResponse.SC_OK,
                        HttpServletResponse.SC_NOT_FOUND
                });
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int[] statusCodes = {
                HttpServletResponse.SC_CREATED,
                HttpServletResponse.SC_BAD_REQUEST
        };
        List<String> errors = new ArrayList<>();
        String name = req.getParameter("name");
        Staff result = null;
        Long roleId = req.getParameter("roleId") == null ? null :
                Long.valueOf(req.getParameter("roleId"));
        if (name == null || name.trim().isEmpty()) {
            errors.add("Name cannot be empty");
        }
        if (roleId == null || roleId < 0) {
            errors.add("Invalid roleID");
        }
        if (errors.isEmpty()) {
            StaffRoleDao staffRoleDao = new StaffRoleDaoImpl();
            StaffRole staffRole = staffRoleDao.getById(roleId);
            if (staffRole == null) {
                errors.add("Role does not exist");
            }
            if (errors.isEmpty()) {
                StaffDao staffDao = new StaffDaoImpl();
                Staff staff = Staff.builder()
                        .name(name)
                        .role(staffRole)
                        .build();
                result = staffDao.save(staff);
            }
        }
        ServletUtil.sendPayloadResponse(res,
                errors,
                result == null ? List.of() : List.of(result),
                statusCodes);
    }

    public void doPatch(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int[] statusCodes = {
                HttpServletResponse.SC_NO_CONTENT,
                HttpServletResponse.SC_BAD_REQUEST
        };
        List<String> errors = new ArrayList<>();
        Staff result = null;
        Long id = ServletUtil.getIdFromRequest(req, errors);
        String name = req.getParameter("name");
        if (errors.isEmpty()) {
            StaffDao staffDao = new StaffDaoImpl();
            Staff staff = staffDao.getById(id);
            if (staff == null) {
                errors.add("Staff does not exist");
                statusCodes[1] = HttpServletResponse.SC_NOT_FOUND;
            }
            if (errors.isEmpty()) {
                Long roleId = req.getParameter("roleId") == null ? null :
                        Long.valueOf(req.getParameter("roleId"));
                if (roleId != null && roleId > 0) {
                    StaffRoleDao staffRoleDao = new StaffRoleDaoImpl();
                    StaffRole staffRole = staffRoleDao.getById(roleId);
                    if (staffRole == null) {
                        errors.add("Role does not exist");
                        statusCodes[1] = HttpServletResponse.SC_NOT_FOUND;
                    }
                    if (errors.isEmpty()) {
                        staff.setRole(staffRole);
                        if (name != null && !name.trim().isEmpty()) {
                            staff.setName(name);
                        }
                        result = staffDao.save(staff);
                        if (result == null) {
                            errors.add("Failed to update staff");
                        }
                    }
                }
            }
        }
        ServletUtil.sendPayloadResponse(res,
                errors,
                result == null ? List.of() : List.of(result),
                statusCodes);
    }

    public void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int[] statusCodes = {
                HttpServletResponse.SC_NO_CONTENT,
                HttpServletResponse.SC_BAD_REQUEST
        };
        List<String> errors = new ArrayList<>();
        Long id = ServletUtil.getIdFromRequest(req, errors);
        if (errors.isEmpty()) {
            StaffDao staffDao = new StaffDaoImpl();
            if (!staffDao.delete(id)) {
                errors.add("Failed to delete staff");
            }
        }
        ServletUtil.sendPlainResponse(res, errors, statusCodes);
    }
}
