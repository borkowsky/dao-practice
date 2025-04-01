package net.rewerk.dbrest.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.rewerk.dbrest.model.dao.LuggageDao;
import net.rewerk.dbrest.model.dao.impl.LuggageDaoImpl;
import net.rewerk.dbrest.model.entity.Luggage;
import net.rewerk.dbrest.model.entity.User;
import net.rewerk.dbrest.util.ServletUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/luggage")
public class LuggageController extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int[] statusCodes = {
                HttpServletResponse.SC_OK,
                HttpServletResponse.SC_BAD_REQUEST
        };
        List<String> errors = new ArrayList<>();
        List<Luggage> result = new ArrayList<>();
        User user = (User) request.getAttribute("user");
        Long id = request.getParameter("id") == null ? null :
                Long.parseLong(request.getParameter("id"));
        if (user == null) {
            errors.add("Unauthorized");
        }
        if (errors.isEmpty()) {
            LuggageDao luggageDao = new LuggageDaoImpl();
            if (id != null) {
                Luggage luggage = luggageDao.getById(id);
                if (luggage != null) {
                    result.add(luggage);
                }
            } else {
                result = luggageDao.findAll();
            }
        }
        ServletUtil.sendPayloadResponse(response,
                errors,
                result,
                statusCodes);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int[] statusCodes = {
                HttpServletResponse.SC_CREATED,
                HttpServletResponse.SC_BAD_REQUEST
        };
        List<String> errors = new ArrayList<>();
        User user = (User) request.getAttribute("user");
        if (user == null) {
            errors.add("Unauthorized");
            statusCodes[1] = HttpServletResponse.SC_UNAUTHORIZED;
        }
        Luggage result = null;
        Integer weight = request.getParameter("weight") == null ? null :
                Integer.parseInt(request.getParameter("weight"));
        if (weight == null || weight < 0) {
            errors.add("Invalid weight value");
        }
        if (errors.isEmpty()) {
            LuggageDao luggageDao = new LuggageDaoImpl();
            Luggage luggage = Luggage.builder()
                    .userId(user.getId())
                    .weight(weight)
                    .build();
            result = luggageDao.save(luggage);
        }
        ServletUtil.sendPayloadResponse(
                response,
                errors,
                result == null ? List.of() : List.of(result),
                statusCodes);
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int[] statusCodes = {
                HttpServletResponse.SC_NO_CONTENT,
                HttpServletResponse.SC_BAD_REQUEST
        };
        User user = (User) request.getAttribute("user");
        List<String> errors = new ArrayList<>();
        Long id = ServletUtil.getIdFromRequest(request, errors);
        if (user == null) {
            errors.add("Unauthorized");
            statusCodes[1] = HttpServletResponse.SC_UNAUTHORIZED;
        }
        if (errors.isEmpty()) {
            LuggageDao luggageDao = new LuggageDaoImpl();
            Luggage luggage = luggageDao.getById(id);
            if (luggage == null) {
                errors.add("Luggage not found");
                statusCodes[1] = HttpServletResponse.SC_NOT_FOUND;
            } else if (!luggage.getUserId().equals(user.getId())) {
                errors.add("Forbidden");
                statusCodes[1] = HttpServletResponse.SC_FORBIDDEN;
            }
            if (errors.isEmpty()) {
                if (!luggageDao.delete(id)) {
                    errors.add("Unable to delete luggage");
                }
            }
        }
        ServletUtil.sendPlainResponse(response, errors, statusCodes);
    }
}
