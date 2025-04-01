package net.rewerk.dbrest.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.rewerk.dbrest.model.dao.UserDao;
import net.rewerk.dbrest.model.dao.impl.UserDaoImpl;
import net.rewerk.dbrest.model.dto.AccessTokenDto;
import net.rewerk.dbrest.model.dto.response.AuthResponseDto;
import net.rewerk.dbrest.model.dto.mapper.AccessTokenMapper;
import net.rewerk.dbrest.model.dto.mapper.UserMapper;
import net.rewerk.dbrest.model.entity.User;
import net.rewerk.dbrest.service.JWTService;
import net.rewerk.dbrest.service.PasswordService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int statusCode = HttpServletResponse.SC_OK;
        List<String> errors = new ArrayList<>();
        AccessTokenDto accessToken = null;
        User user = null;
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");
            DecodedJWT jwt = JWTService.validateToken(token);
            if (jwt == null) {
                statusCode = HttpServletResponse.SC_UNAUTHORIZED;
                errors.add("Invalid token");
            }
            if (errors.isEmpty()) {
                Long userId = jwt.getClaim("UID").asLong();
                if (userId == null) {
                    statusCode = HttpServletResponse.SC_UNAUTHORIZED;
                    errors.add("Invalid user ID");
                }
                if (errors.isEmpty()) {
                    UserDao userDao = new UserDaoImpl();
                    user = userDao.getById(userId);
                    if (user == null) {
                        statusCode = HttpServletResponse.SC_UNAUTHORIZED;
                        errors.add("Invalid user");
                    } else {
                        accessToken = AccessTokenMapper.toDto(token);
                    }
                }
            }
        } else {
            if (username == null || password == null) {
                errors.add("Username and password are required");
                statusCode = HttpServletResponse.SC_BAD_REQUEST;
            }
            UserDao userDao = new UserDaoImpl();
            if (errors.isEmpty()) {
                List<User> users = userDao.findByUsername(username);
                if (users.size() != 1) {
                    errors.add("Unable to find user");
                    statusCode = HttpServletResponse.SC_UNAUTHORIZED;
                } else {
                    user = users.get(0);
                }
                if (user == null) {
                    errors.add("User not found");
                    statusCode = HttpServletResponse.SC_UNAUTHORIZED;
                }
                if (user != null) {
                    if (!PasswordService.verifyPassword(password, user.getPassword())) {
                        errors.add("Invalid credentials");
                        statusCode = HttpServletResponse.SC_UNAUTHORIZED;
                    }
                    if (errors.isEmpty()) {
                        accessToken = JWTService.signToken(user);
                        statusCode = HttpServletResponse.SC_CREATED;
                    }
                }
                if (accessToken == null) {
                    errors.add("Unable to generate access token");
                    statusCode = HttpServletResponse.SC_UNAUTHORIZED;
                }
            }
        }
        Gson gson = new Gson();
        AuthResponseDto authResultDto = AuthResponseDto.builder()
                .success(errors.isEmpty())
                .message(errors.isEmpty() ? null : String.join(", ", errors))
                .user(user == null ? null : UserMapper.toDto(user))
                .accessToken(accessToken)
                .build();
        response.setStatus(statusCode);
        PrintWriter out = response.getWriter();
        out.println(gson.toJson(authResultDto));
        out.flush();
        out.close();
    }
}
