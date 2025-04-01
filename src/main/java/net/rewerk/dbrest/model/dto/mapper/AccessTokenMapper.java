package net.rewerk.dbrest.model.dto.mapper;

import com.auth0.jwt.interfaces.DecodedJWT;
import net.rewerk.dbrest.model.dto.AccessTokenDto;
import net.rewerk.dbrest.service.JWTService;

public abstract class AccessTokenMapper {
    public static AccessTokenDto toDto(String accessToken) {
        DecodedJWT decodedJWT = JWTService.validateToken(accessToken);
        if (decodedJWT == null) {
            return null;
        }
        return AccessTokenDto.builder()
                .accessToken(accessToken)
                .createdAt(decodedJWT.getIssuedAt().toInstant().toString())
                .expiresIn(decodedJWT.getExpiresAt().toInstant().toString())
                .build();
    }
}
