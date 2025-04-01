package net.rewerk.dbrest.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.rewerk.dbrest.helper.ConfigLoader;
import net.rewerk.dbrest.model.dao.UserDao;
import net.rewerk.dbrest.model.dao.impl.UserDaoImpl;
import net.rewerk.dbrest.model.entity.User;
import net.rewerk.dbrest.service.JWTService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Properties;

public class AuthenticationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        Properties config = ConfigLoader.getInstance().getProperties();
        String[] publicRoutes = config.getProperty("routes.public").split(",");
        if (Arrays.asList(publicRoutes).contains(httpRequest.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendAuthenticationError(httpRequest, httpResponse);
            chain.doFilter(request, response);
        } else {
            DecodedJWT jwt = JWTService.validateToken(authHeader.replace("Bearer ", ""));
            if (jwt == null) {
                sendAuthenticationError(httpRequest, httpResponse);
                chain.doFilter(request, response);
            } else {
                Long userId = jwt.getClaim("UID").asLong();
                if (userId == null) {
                    sendAuthenticationError(httpRequest, httpResponse);
                    chain.doFilter(request, response);
                } else {
                    UserDao userDao = new UserDaoImpl();
                    User user = userDao.getById(userId);
                    if (user != null) {
                        request.setAttribute("user", user);
                        chain.doFilter(request, response);
                    } else {
                        sendAuthenticationError(httpRequest, httpResponse);
                        chain.doFilter(request, response);
                    }
                }
            }
        }
    }

    private void sendAuthenticationError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println("{\"error\": \"Authentication required\"}");
        out.close();
    }
}
