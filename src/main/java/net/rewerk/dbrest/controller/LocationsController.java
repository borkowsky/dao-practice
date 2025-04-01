package net.rewerk.dbrest.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.rewerk.dbrest.model.dao.LocationDao;
import net.rewerk.dbrest.model.dao.impl.LocationDaoImpl;
import net.rewerk.dbrest.model.entity.Location;
import net.rewerk.dbrest.util.ServletUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/locations")
public class LocationsController extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int[] statusCodes = {
                HttpServletResponse.SC_OK,
                HttpServletResponse.SC_BAD_REQUEST
        };
        LocationDao locationDao = new LocationDaoImpl();
        List<Location> locations = locationDao.findAll();
        ServletUtil.sendPayloadResponse(res, List.of(), locations, statusCodes);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int[] statusCodes = {
                HttpServletResponse.SC_CREATED,
                HttpServletResponse.SC_BAD_REQUEST
        };
        List<String> errors = new ArrayList<>();
        Location result = null;
        String address = req.getParameter("address");
        String country = req.getParameter("country");
        if (address == null || address.trim().isEmpty()) {
            errors.add("Address is required");
        }
        if (country == null || country.trim().isEmpty()) {
            errors.add("Country is required");
        }
        if (errors.isEmpty()) {
            Location location = Location.builder()
                    .address(address)
                    .country(country)
                    .build();
            LocationDao locationDao = new LocationDaoImpl();
            result = locationDao.save(location);
        }
        if (result == null) {
            errors.add("Failed to save location");
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
        Long id = ServletUtil.getIdFromRequest(req, errors);
        String address = req.getParameter("address");
        String country = req.getParameter("country");
        if (errors.isEmpty()) {
            LocationDao locationDao = new LocationDaoImpl();
            Location location = locationDao.getById(id);
            if (location == null) {
                errors.add("Location not found");
                statusCodes[1] = HttpServletResponse.SC_NOT_FOUND;
            }
            if (errors.isEmpty()) {
                if (address != null && !address.trim().isEmpty()) {
                    location.setAddress(address);
                }
                if (country != null && !country.trim().isEmpty()) {
                    location.setCountry(country);
                }
                if (locationDao.save(location) == null) {
                    errors.add("Failed to save location");
                }
            }
        }
        ServletUtil.sendPlainResponse(res,
                errors,
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
            LocationDao locationDao = new LocationDaoImpl();
            if (!locationDao.delete(id)) {
                errors.add("Failed to delete location");
            }
        }
        ServletUtil.sendPlainResponse(res,
                errors,
                statusCodes);
    }
}
