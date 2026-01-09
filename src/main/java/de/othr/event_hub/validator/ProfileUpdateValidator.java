package de.othr.event_hub.validator;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import de.othr.event_hub.dto.UpdateUserInfoDto;
import de.othr.event_hub.model.User;
import de.othr.event_hub.service.UserService;

@Component
public class ProfileUpdateValidator implements Validator {
    private UserService userService;

    public ProfileUpdateValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UpdateUserInfoDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UpdateUserInfoDto userInfoDto = (UpdateUserInfoDto) target;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());

        if (!userInfoDto.getUsername().equals(user.getUsername())) {
            if (userService.usernameExists(userInfoDto.getUsername())) {
                errors.rejectValue("username", "username.already.exists");
            }
        }

        if (!userInfoDto.getEmail().equals(user.getEmail())) {
            if (userService.emailExists(userInfoDto.getEmail())) {
                errors.rejectValue("email", "email.already.exists");
            }
        }
    }
}
