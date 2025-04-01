package net.rewerk.dbrest.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.rewerk.dbrest.model.dao.StaffRoleDao;
import net.rewerk.dbrest.model.dao.impl.StaffRoleDaoImpl;
import net.rewerk.dbrest.model.entity.StaffRole;
import net.rewerk.dbrest.util.ServletUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/staff-roles")
public class StaffRolesController extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        StaffRoleDao staffRoleDao = new StaffRoleDaoImpl();
        List<StaffRole> staffRoles = staffRoleDao.findAll();
        ServletUtil.sendPayloadResponse(res,
                List.of(),
                staffRoles,
                new int[]{
                        HttpServletResponse.SC_OK,
                        HttpServletResponse.SC_NOT_FOUND
                }
        );
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        List<String> errors = new ArrayList<>();
        StaffRole result = null;
        String name = req.getParameter("name");
        if (name == null) {
            errors.add("Name is required");
        }
        if (errors.isEmpty()) {
            StaffRole staffRole = StaffRole.builder()
                    .name(name)
                    .build();
            StaffRoleDao staffRoleDao = new StaffRoleDaoImpl();
            result = staffRoleDao.save(staffRole);
        }
        if (result == null) {
            errors.add("Failed to save staff role");
        }
        ServletUtil.sendPayloadResponse(res,
                errors,
                result == null ? List.of() : List.of(result),
                new int[]{
                        HttpServletResponse.SC_CREATED,
                        HttpServletResponse.SC_BAD_REQUEST
                });
    }

    public void doPatch(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int[] statusCodes = {
                HttpServletResponse.SC_NO_CONTENT,
                HttpServletResponse.SC_BAD_REQUEST
        };
        List<String> errors = new ArrayList<>();
        Long id = ServletUtil.getIdFromRequest(req, errors);
        String name = req.getParameter("name");
        if (errors.isEmpty()) {
            StaffRoleDao staffRoleDao = new StaffRoleDaoImpl();
            StaffRole currentStaffRole = staffRoleDao.getById(id);
            if (currentStaffRole == null) {
                errors.add("Staff role not found");
                statusCodes[1] = HttpServletResponse.SC_NOT_FOUND;
            }
            if (errors.isEmpty()) {
                if (name != null && !name.trim().isEmpty()) {
                    currentStaffRole.setName(name);
                }
                if (staffRoleDao.save(currentStaffRole) == null) {
                    errors.add("Failed to save staff role");
                }
            }
        }
        ServletUtil.sendPlainResponse(res, errors, statusCodes);
    }

    public void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int[] statusCodes = {
                HttpServletResponse.SC_NO_CONTENT,
                HttpServletResponse.SC_BAD_REQUEST
        };
        List<String> errors = new ArrayList<>();
        Long id = ServletUtil.getIdFromRequest(req, errors);
        if (errors.isEmpty()) {
            StaffRoleDao staffRoleDao = new StaffRoleDaoImpl();
            if (!staffRoleDao.delete(id)) {
                errors.add("Failed to delete staff role");
            }
        }
        ServletUtil.sendPlainResponse(res, errors, statusCodes);
    }
}
