package net.rewerk.dbrest.model.entity;

import lombok.*;
import net.rewerk.dbrest.enumerator.UserGender;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private Long id;
    private String username;
    private String password;
    private String name;
    private Integer age;
    private String email;
    private String passport;
    private UserGender gender;
    private String createdAt;
    private String updatedAt;
}
