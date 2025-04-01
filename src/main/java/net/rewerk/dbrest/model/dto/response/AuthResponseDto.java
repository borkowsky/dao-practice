package net.rewerk.dbrest.model.dto.response;

import lombok.Builder;
import lombok.Data;
import net.rewerk.dbrest.model.dto.AccessTokenDto;
import net.rewerk.dbrest.model.dto.UserDto;

@Data
@Builder
public class AuthResponseDto {
    private Boolean success;
    private String message;
    private UserDto user;
    private AccessTokenDto accessToken;
}
