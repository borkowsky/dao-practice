package net.rewerk.dbrest.model.dto;

import lombok.Builder;
import lombok.Data;
import net.rewerk.dbrest.enumerator.UserGender;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String passport;
    private Integer age;
    private UserGender gender;
}
