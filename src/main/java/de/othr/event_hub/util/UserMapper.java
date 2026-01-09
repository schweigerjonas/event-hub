package de.othr.event_hub.util;

import org.springframework.stereotype.Component;

import de.othr.event_hub.dto.UserDto;
import de.othr.event_hub.model.User;

@Component
public class UserMapper {
    public User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        User user = new User();

        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setActive(1);
        user.setUsing2FA(userDto.isUsing2FA());

        return user;
    }

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setEmail(user.getEmail());
        userDto.setUsername(user.getUsername());
        userDto.setUsing2FA(user.isUsing2FA());

        if (user.getAuthorities() != null && !user.getAuthorities().isEmpty()) {
            String role = user.getAuthorities().get(0).getDescription();
            userDto.setRole(role);
        }

        return userDto;
    }
}
