package net.rewerk.dbrest.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.rewerk.dbrest.model.dao.AirplaneDao;
import net.rewerk.dbrest.model.dao.LocationDao;
import net.rewerk.dbrest.model.dao.RouteDao;
import net.rewerk.dbrest.model.dao.impl.AirplaneDaoImpl;
import net.rewerk.dbrest.model.dao.impl.LocationDaoImpl;
import net.rewerk.dbrest.model.dao.impl.RouteDaoImpl;
import net.rewerk.dbrest.model.entity.Airplane;
import net.rewerk.dbrest.model.entity.Location;
import net.rewerk.dbrest.model.entity.Route;
import net.rewerk.dbrest.util.ServletUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/routes")
public class RoutesController extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RouteDao routeDao = new RouteDaoImpl();
        List<Route> routes = routeDao.findAll();
        ServletUtil.sendPayloadResponse(resp,
                List.of(),
                routes,
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
        Route result = null;
        Long airplaneId = req.getParameter("airplaneId") == null ? null :
                Long.parseLong(req.getParameter("airplaneId"));
        String departureTime = req.getParameter("departureTime");
        String arrivalTime = req.getParameter("arrivalTime");
        Long departureLocationId = req.getParameter("departureLocationId") == null ? null :
                Long.parseLong(req.getParameter("departureLocationId"));
        Long arrivalLocationId = req.getParameter("arrivalLocationId") == null ? null :
                Long.parseLong(req.getParameter("arrivalLocationId"));
        if (airplaneId == null || airplaneId < 0) {
            errors.add("airplaneId is required");
        }
        if (departureTime == null || departureTime.isEmpty()) {
            errors.add("departureTime is required");
        }
        if (arrivalTime == null || arrivalTime.isEmpty()) {
            errors.add("arrivalTime is required");
        }
        if (departureLocationId == null || departureLocationId < 0) {
            errors.add("departureLocationId is required");
        }
        if (arrivalLocationId == null || arrivalLocationId < 0) {
            errors.add("arrivalLocationId is required");
        }
        if (errors.isEmpty()) {
            if (arrivalLocationId.equals(departureLocationId)) {
                errors.add("departureLocationId and arrivalLocationId cannot be the same");
            } else {
                AirplaneDao airplaneDao = new AirplaneDaoImpl();
                Airplane airplane = airplaneDao.getById(airplaneId);
                if (airplane == null) {
                    errors.add("Airplane not found");
                    statusCodes[1] = HttpServletResponse.SC_NOT_FOUND;
                }
                if (errors.isEmpty()) {
                    LocationDao locationDao = new LocationDaoImpl();
                    Location departureLocation = locationDao.getById(departureLocationId);
                    if (departureLocation == null) {
                        errors.add("Departure location not found");
                        statusCodes[1] = HttpServletResponse.SC_NOT_FOUND;
                    }
                    if (errors.isEmpty()) {
                        Location arrivalLocation = locationDao.getById(arrivalLocationId);
                        if (arrivalLocation == null) {
                            errors.add("Arrival location not found");
                            statusCodes[1] = HttpServletResponse.SC_NOT_FOUND;
                        }
                        if (errors.isEmpty()) {
                            RouteDao routeDao = new RouteDaoImpl();
                            Route route = Route.builder()
                                    .airplane(airplane)
                                    .departureTime(departureTime)
                                    .arrivalTime(arrivalTime)
                                    .departureLocation(departureLocation)
                                    .arrivalLocation(arrivalLocation)
                                    .build();
                            result = routeDao.save(route);
                            if (result == null) {
                                errors.add("Failed to save route");
                            }
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

    public void doPatch(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int[] statusCodes = {
                HttpServletResponse.SC_NO_CONTENT,
                HttpServletResponse.SC_BAD_REQUEST
        };
        List<String> errors = new ArrayList<>();
        Route result = null;
        Long id = ServletUtil.getIdFromRequest(req, errors);
        Long airplaneId = req.getParameter("airplaneId") == null ? null :
                Long.parseLong(req.getParameter("airplaneId"));
        String departureTime = req.getParameter("departureTime");
        String arrivalTime = req.getParameter("arrivalTime");
        Long departureLocationId = req.getParameter("departureLocationId") == null ? null :
                Long.parseLong(req.getParameter("departureLocationId"));
        Long arrivalLocationId = req.getParameter("arrivalLocationId") == null ? null :
                Long.parseLong(req.getParameter("arrivalLocationId"));
        if (errors.isEmpty()) {
            RouteDao routeDao = new RouteDaoImpl();
            Route route = routeDao.getById(id);
            if (route == null) {
                errors.add("Route not found");
                statusCodes[1] = HttpServletResponse.SC_NOT_FOUND;
            }
            if (errors.isEmpty()) {
                LocationDao locationDao = new LocationDaoImpl();
                Airplane airplane = null;
                Location departureLocation = null;
                Location arrivalLocation = null;
                if (airplaneId != null && airplaneId > 0 && !airplaneId.equals(route.getAirplane().getId())) {
                    AirplaneDao airplaneDao = new AirplaneDaoImpl();
                    airplane = airplaneDao.getById(airplaneId);
                    if (airplane == null) {
                        errors.add("Airplane not found");
                        statusCodes[1] = HttpServletResponse.SC_NOT_FOUND;
                    }
                }
                if (errors.isEmpty()) {
                    if (departureLocationId != null
                            && departureLocationId > 0
                            && !departureLocationId.equals(route.getDepartureLocation().getId())) {
                        departureLocation = locationDao.getById(departureLocationId);
                        if (departureLocation == null) {
                            errors.add("Departure location not found");
                            statusCodes[1] = HttpServletResponse.SC_NOT_FOUND;
                        }
                    }
                }
                if (errors.isEmpty()) {
                    if (arrivalLocationId != null
                            && arrivalLocationId > 0
                            && !arrivalLocationId.equals(route.getArrivalLocation().getId())) {
                        arrivalLocation = locationDao.getById(arrivalLocationId);
                        if (arrivalLocation == null) {
                            errors.add("Arrival location not found");
                            statusCodes[1] = HttpServletResponse.SC_NOT_FOUND;
                        }
                    }
                }
                if (errors.isEmpty()) {
                    if (airplane != null) route.setAirplane(airplane);
                    if (departureTime != null) route.setDepartureTime(departureTime);
                    if (arrivalTime != null) route.setArrivalTime(arrivalTime);
                    if (departureLocation != null) route.setDepartureLocation(departureLocation);
                    if (arrivalLocation != null) route.setArrivalLocation(arrivalLocation);
                }
                result = routeDao.save(route);
            }
        }
        ServletUtil.sendPayloadResponse(
                res,
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
            RouteDao routeDao = new RouteDaoImpl();
            if (!routeDao.delete(id)) {
                errors.add("Failed to delete route");
            }
        }
        ServletUtil.sendPlainResponse(res, errors, statusCodes);
    }
}
