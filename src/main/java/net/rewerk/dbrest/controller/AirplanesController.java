package net.rewerk.dbrest.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.rewerk.dbrest.model.dao.AirplaneDao;
import net.rewerk.dbrest.model.dao.StaffDao;
import net.rewerk.dbrest.model.dao.impl.AirplaneDaoImpl;
import net.rewerk.dbrest.model.dao.impl.StaffDaoImpl;
import net.rewerk.dbrest.model.entity.Airplane;
import net.rewerk.dbrest.model.entity.Staff;
import net.rewerk.dbrest.util.CommonUtil;
import net.rewerk.dbrest.util.ServletUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@WebServlet("/airplanes")
public class AirplanesController extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int[] statusCodes = {
                HttpServletResponse.SC_OK,
                HttpServletResponse.SC_BAD_REQUEST
        };
        AirplaneDao airplaneDao = new AirplaneDaoImpl();
        List<Airplane> airplanes = airplaneDao.findAll();
        ServletUtil.sendPayloadResponse(resp, List.of(), airplanes, statusCodes);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int[] statusCodes = {
                HttpServletResponse.SC_CREATED,
                HttpServletResponse.SC_BAD_REQUEST
        };
        List<String> errors = new ArrayList<>();
        Airplane result = null;
        String name = req.getParameter("name");
        String number = req.getParameter("number");
        String[] staffIds = req.getParameterValues("staffId");
        if (name == null || name.trim().isEmpty()) {
            errors.add("Name is required");
        }
        if (number == null || number.trim().isEmpty()) {
            errors.add("Number is required");
        }
        if (staffIds == null || staffIds.length == 0) {
            errors.add("RoleID is required");
        }
        if (errors.isEmpty()) {
            StaffDao staffDao = new StaffDaoImpl();
            List<Staff> staff = staffDao.findByIds(Stream.of(staffIds)
                    .map(Long::valueOf).collect(Collectors.toList())
            );
            if (staff.size() != staffIds.length) {
                errors.add("Invalid staff ID");
            }
            if (errors.isEmpty()) {
                AirplaneDao airplaneDao = new AirplaneDaoImpl();
                Airplane airplane = Airplane.builder()
                        .name(name)
                        .number(number)
                        .staff(staff)
                        .build();
                result = airplaneDao.save(airplane);
            }
        }
        if (result == null) {
            errors.add("Failed to save airplane");
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
        Airplane result = null;
        Long id = ServletUtil.getIdFromRequest(req, errors);
        String name = req.getParameter("name");
        String number = req.getParameter("number");
        String[] staffIds = req.getParameterValues("staffId");
        if (errors.isEmpty()) {
            AirplaneDao airplaneDao = new AirplaneDaoImpl();
            Airplane airplane = airplaneDao.getById(id);
            if (airplane == null) {
                errors.add("Airplane not found");
                statusCodes[1] = HttpServletResponse.SC_NOT_FOUND;
            }
            if (errors.isEmpty()) {
                if (name != null && !name.trim().isEmpty()) airplane.setName(name);
                if (number != null && !number.trim().isEmpty()) airplane.setNumber(number);
                if (staffIds != null && staffIds.length > 0) {
                    List<Long> staffIdsList = Stream.of(staffIds).map(Long::valueOf).collect(Collectors.toList());
                    if (!CommonUtil.isListEquals(staffIdsList, airplane.getStaff().stream()
                            .map(Staff::getId)
                            .collect(Collectors.toList())
                    )) {
                        StaffDao staffDao = new StaffDaoImpl();
                        List<Staff> newStaff = staffDao.findByIds(staffIdsList);
                        airplane.setStaff(newStaff);
                    }
                    result = airplaneDao.save(airplane);
                    if (result == null) {
                        errors.add("Failed to save airplane");
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
            AirplaneDao airplaneDao = new AirplaneDaoImpl();
            if (!airplaneDao.delete(id)) {
                errors.add("Failed to delete airplane");
            }
        }
        ServletUtil.sendPlainResponse(res,
                errors,
                statusCodes);
    }
}
