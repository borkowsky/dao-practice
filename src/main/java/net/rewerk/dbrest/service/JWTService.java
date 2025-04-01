package net.rewerk.dbrest.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import net.rewerk.dbrest.helper.ConfigLoader;
import net.rewerk.dbrest.model.dto.AccessTokenDto;
import net.rewerk.dbrest.model.entity.User;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Properties;

public abstract class JWTService {

    public static AccessTokenDto signToken(User user) {
        Properties config = ConfigLoader.getInstance().getProperties();
        Date expiresIn = Date.from(Instant.now().plus(
                Integer.parseInt(config.getProperty("jwt.lifetime")),
                ChronoUnit.DAYS));
        String token = JWT.create()
                .withClaim("UID", user.getId())
                .withIssuer(config.getProperty("jwt.issuer"))
                .withIssuedAt(new Date().toInstant())
                .withExpiresAt(expiresIn)
                .sign(Algorithm.HMAC256(config.getProperty("jwt.key")));
        return AccessTokenDto.builder()
                .accessToken(token)
                .createdAt(new Date().toString())
                .expiresIn(expiresIn.toString())
                .build();
    }

    public static DecodedJWT validateToken(String token) {
        Properties config = ConfigLoader.getInstance().getProperties();
        final String ISSUER = config.getProperty("jwt.issuer");
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(config.getProperty("jwt.key")))
                    .withIssuer(ISSUER)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            Instant expiresAt = jwt.getExpiresAtAsInstant();
            String issuer = jwt.getIssuer();
            if (!issuer.equals(ISSUER)) {
                return null;
            }
            if (expiresAt.isBefore(Instant.now())) {
                return null;
            }
            return jwt;
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}
