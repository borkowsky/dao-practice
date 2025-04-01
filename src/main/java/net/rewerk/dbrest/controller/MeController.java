package net.rewerk.dbrest.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.rewerk.dbrest.model.dao.UserDao;
import net.rewerk.dbrest.model.dao.impl.UserDaoImpl;
import net.rewerk.dbrest.model.dto.mapper.UserMapper;
import net.rewerk.dbrest.model.entity.User;
import net.rewerk.dbrest.util.ServletUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/users/me")
public class MeController extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getAttribute("user");
        ServletUtil.sendPayloadResponse(
                resp,
                user == null ? List.of("UNAUTHORIZED") : List.of(),
                user == null ? List.of() : List.of(UserMapper.toDto(user)),
                new int[]{
                        HttpServletResponse.SC_OK,
                        HttpServletResponse.SC_UNAUTHORIZED
                }
        );
    }

    public void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int[] statusCodes = {
                HttpServletResponse.SC_NO_CONTENT,
                HttpServletResponse.SC_BAD_REQUEST
        };
        List<String> errors = new ArrayList<>();
        User user = (User) req.getAttribute("user");
        if (user == null) {
            errors.add("Unauthorized");
            statusCodes[1] = HttpServletResponse.SC_UNAUTHORIZED;
        }
        if (errors.isEmpty()) {
            UserDao userDao = new UserDaoImpl();
            String name = req.getParameter("name");
            Integer age = req.getParameter("age") == null ? null :
                    Integer.parseInt(req.getParameter("age"));
            String email = req.getParameter("email");
            String passport = req.getParameter("passport");
            if (name != null && !name.trim().isEmpty()) user.setName(name);
            if (age != null && age > 0) user.setAge(age);
            if (email != null && !email.trim().isEmpty()) user.setEmail(email);
            if (passport != null && !passport.trim().isEmpty()) user.setPassport(passport);
            user = userDao.save(user);
        }
        ServletUtil.sendPayloadResponse(resp,
                errors,
                user == null ? List.of() : List.of(user),
                statusCodes);
    }
}
