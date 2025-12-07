package de.othr.event_hub.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import de.othr.event_hub.dto.UserDto;
import de.othr.event_hub.service.UserService;

@Component
public class SignupValidator implements Validator {
    private UserService userService;

    public SignupValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDto userDto = (UserDto) target;

        // validate both passwords match
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "user.password.no.match");
            errors.rejectValue("password", "empty.message");
        }

        // validate user credentials are unique
        if (userService.usernameExists(userDto.getUsername())) {
            errors.rejectValue("username", "username.already.exists");
        }
        if (userService.emailExists(userDto.getEmail())) {
            errors.rejectValue("email", "email.already.exists");
        }
    }

}
