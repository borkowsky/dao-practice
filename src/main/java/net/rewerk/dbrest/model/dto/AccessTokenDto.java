package net.rewerk.dbrest.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessTokenDto {
    private String accessToken;
    private String createdAt;
    private String expiresIn;
}
