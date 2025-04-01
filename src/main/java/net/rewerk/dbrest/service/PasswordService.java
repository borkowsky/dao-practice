package net.rewerk.dbrest.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import net.rewerk.dbrest.exception.AuthException;
import net.rewerk.dbrest.helper.ConfigLoader;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Properties;

public abstract class PasswordService {

    public static String encryptPassword(String password) {
        Properties config = ConfigLoader.getInstance().getProperties();
        return BCrypt.with(new SecureRandom())
                .hashToString(Integer.parseInt(config.getProperty("password.strength")), password.toCharArray());
    }

    public static boolean verifyPassword(String password, String hashedPassword) throws AuthException {
        return BCrypt.verifyer()
                .verify(password.getBytes(StandardCharsets.UTF_8), hashedPassword.getBytes(StandardCharsets.UTF_8))
                .verified;
    }
}
