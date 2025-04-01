package net.rewerk.dbrest.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.rewerk.dbrest.enumerator.UserGender;
import net.rewerk.dbrest.model.dao.UserDao;
import net.rewerk.dbrest.model.dao.impl.UserDaoImpl;
import net.rewerk.dbrest.model.entity.User;
import net.rewerk.dbrest.util.ServletUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/registration")
public class RegistrationController extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<String> errors = new ArrayList<>();
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");
        String name = req.getParameter("name");
        String gender = req.getParameter("gender");
        String passport = req.getParameter("passport");
        Integer age = req.getParameter("age") == null ? null : Integer.parseInt(req.getParameter("age"));
        if (username == null || username.trim().isEmpty()) {
            errors.add("Username is required");
        }
        if (password == null || password.trim().isEmpty()) {
            errors.add("Password is required");
        }
        if (email == null || email.trim().isEmpty()) {
            errors.add("Email is required");
        }
        if (name == null || name.trim().isEmpty()) {
            errors.add("Name is required");
        }
        if (gender == null || gender.trim().isEmpty()) {
            errors.add("Gender is required");
        }
        if (passport == null || passport.trim().isEmpty()) {
            errors.add("Passport is required");
        }
        if (age == null || age <= 0) {
            errors.add("Age is required");
        }
        if (errors.isEmpty()) {
            UserDao userDao = new UserDaoImpl();
            try {
                User user = User.builder()
                        .username(username)
                        .password(password)
                        .email(email)
                        .name(name)
                        .age(age)
                        .gender(UserGender.valueOf(gender.toUpperCase()))
                        .passport(passport)
                        .build();
                if (userDao.save(user) == null) {
                    errors.add("Unable to create user");
                }
            } catch (Exception e) {
                errors.add("Unable to create user");
            }
        }
        ServletUtil.sendPlainResponse(resp,
                errors,
                new int[]{
                        HttpServletResponse.SC_CREATED,
                        HttpServletResponse.SC_BAD_REQUEST
                });
    }
}
