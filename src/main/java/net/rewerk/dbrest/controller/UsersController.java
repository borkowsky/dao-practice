package net.rewerk.dbrest.controller;

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
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/users")
public class UsersController extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDao userDao = new UserDaoImpl();
        List<User> users = userDao.findAll();
        ServletUtil.sendPayloadResponse(resp, List.of(), users.stream()
                        .map(UserMapper::toDto)
                        .collect(Collectors.toList()),
                new int[]{
                        HttpServletResponse.SC_OK,
                        HttpServletResponse.SC_NOT_FOUND
                }
        );
    }
}
