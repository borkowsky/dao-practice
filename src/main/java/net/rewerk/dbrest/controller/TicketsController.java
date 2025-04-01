package net.rewerk.dbrest.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.rewerk.dbrest.enumerator.TicketClass;
import net.rewerk.dbrest.model.dao.*;
import net.rewerk.dbrest.model.dao.impl.*;
import net.rewerk.dbrest.model.dto.mapper.UserMapper;
import net.rewerk.dbrest.model.entity.*;
import net.rewerk.dbrest.util.ServletUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/tickets")
public class TicketsController extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int[] statusCodes = {
                HttpServletResponse.SC_OK,
                HttpServletResponse.SC_BAD_REQUEST
        };
        List<String> errors = new ArrayList<>();
        List<Ticket> result = new ArrayList<>();
        User user = (User) req.getAttribute("user");
        Long id = req.getParameter("id") == null ? null : Long.parseLong(req.getParameter("id"));
        if (user == null) {
            errors.add("Unauthorized");
            statusCodes[1] = HttpServletResponse.SC_UNAUTHORIZED;
        }
        if (errors.isEmpty()) {
            TicketDao ticketDao = new TicketDaoImpl();
            if (id != null && id > 0) {
                Ticket ticket = ticketDao.getById(id);
                if (ticket != null) {
                    result.add(ticket);
                } else {
                    errors.add("Ticket with id " + id + " not found");
                    statusCodes[1] = HttpServletResponse.SC_NOT_FOUND;
                }
            } else {
                result = ticketDao.findByUserId(user.getId());
            }
        }
        ServletUtil.sendPayloadResponse(resp, errors, result, statusCodes);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int[] statusCodes = {
                HttpServletResponse.SC_CREATED,
                HttpServletResponse.SC_BAD_REQUEST
        };
        List<String> errors = new ArrayList<>();
        Ticket result = null;
        User user = (User) req.getAttribute("user");
        String ticketClass = req.getParameter("ticketClass");
        Long routeId = req.getParameter("routeId") == null ? null :
                Long.parseLong(req.getParameter("routeId"));
        Long staffId = req.getParameter("staffId") == null ? null :
                Long.parseLong(req.getParameter("staffId"));
        Long luggageId = req.getParameter("luggageId") == null ? null :
                Long.parseLong(req.getParameter("luggageId"));
        TicketClass ticketClassEnum = null;
        Route route = null;
        Staff staff = null;
        Luggage luggage = null;
        if (user == null) {
            errors.add("Unauthorized");
            statusCodes[1] = HttpServletResponse.SC_UNAUTHORIZED;
        }
        if (ticketClass == null) {
            errors.add("Ticket class is required");
        }
        if (routeId == null || routeId < 0) {
            errors.add("Route ID is required");
        }
        if (staffId == null || staffId < 0) {
            errors.add("Staff ID is required");
        }
        if (errors.isEmpty()) {
            try {
                ticketClassEnum = TicketClass.valueOf(ticketClass.toUpperCase());
            } catch (Exception e) {
                errors.add("Ticket class is invalid");
            }
        }
        if (errors.isEmpty()) {
            RouteDao routeDao = new RouteDaoImpl();
            route = routeDao.getById(routeId);
            if (route == null) {
                errors.add("Route does not exist");
                statusCodes[1] = HttpServletResponse.SC_NOT_FOUND;
            }
        }
        if (errors.isEmpty()) {
            StaffDao staffDao = new StaffDaoImpl();
            staff = staffDao.getById(staffId);
            if (staff == null) {
                errors.add("Staff does not exist");
                statusCodes[1] = HttpServletResponse.SC_NOT_FOUND;
            }
        }
        if (errors.isEmpty()) {
            if (luggageId != null && luggageId > 0) {
                LuggageDao luggageDao = new LuggageDaoImpl();
                luggage = luggageDao.getById(luggageId);
                if (luggage == null || !luggage.getUserId().equals(user.getId())) {
                    errors.add("Luggage does not exist");
                    statusCodes[1] = HttpServletResponse.SC_NOT_FOUND;
                    luggage = null;
                }
            }
        }
        if (errors.isEmpty()) {
            TicketDao ticketDao = new TicketDaoImpl();
            Ticket ticket = Ticket.builder()
                    .ticketClass(ticketClassEnum)
                    .route(route)
                    .staff(staff)
                    .luggage(luggage)
                    .user(UserMapper.toDto(user))
                    .passport(user.getPassport())
                    .build();
            result = ticketDao.save(ticket);
        }
        ServletUtil.sendPayloadResponse(
                resp,
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
            TicketDao ticketDao = new TicketDaoImpl();
            if (!ticketDao.delete(id)) {
                errors.add("Failed to delete ticket");
            }
        }
        ServletUtil.sendPlainResponse(res,
                errors,
                statusCodes);
    }
}
