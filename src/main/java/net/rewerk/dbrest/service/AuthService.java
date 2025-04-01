package net.rewerk.dbrest.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.rewerk.dbrest.exception.AuthException;
import net.rewerk.dbrest.model.dto.AccessTokenDto;
import net.rewerk.dbrest.model.entity.User;

public abstract class AuthService {
    public static AccessTokenDto authenticate(
            User user,
            HttpServletRequest request,
            HttpServletResponse response) throws AuthException {
        try {
            return JWTService.signToken(user);
        } catch (Exception e) {
            throw new AuthException("Unable to authenticate user");
        }
    }

    public static void logout(HttpServletRequest request, HttpServletResponse response) throws AuthException {
        HttpSession session = request.getSession(false);
        session.invalidate();
    }
}
