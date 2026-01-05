package de.othr.event_hub.util;

import org.springframework.stereotype.Component;

import de.othr.event_hub.dto.UserDto;
import de.othr.event_hub.model.User;

@Component
public class UserMapper {
    public User toEntity(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setActive(1);
        user.setUsing2FA(userDto.isUsing2FA());

        return user;
    }
}
