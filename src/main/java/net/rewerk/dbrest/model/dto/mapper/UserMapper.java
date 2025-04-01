package net.rewerk.dbrest.model.dto.mapper;

import net.rewerk.dbrest.model.dto.UserDto;
import net.rewerk.dbrest.model.entity.User;

public abstract class UserMapper {
    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .passport(user.getPassport())
                .age(user.getAge())
                .gender(user.getGender())
                .build();
    }

    public static User fromDto(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .passport(userDto.getPassport())
                .age(userDto.getAge())
                .gender(userDto.getGender())
                .build();
    }
}
