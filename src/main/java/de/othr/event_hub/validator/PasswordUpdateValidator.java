package de.othr.event_hub.validator;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import de.othr.event_hub.dto.UpdatePasswordDto;
import de.othr.event_hub.model.User;
import de.othr.event_hub.service.UserService;

@Component
public class PasswordUpdateValidator implements Validator {
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    public PasswordUpdateValidator(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UpdatePasswordDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UpdatePasswordDto dto = (UpdatePasswordDto) target;

        if (dto.getNewPassword() != null && !dto.getNewPassword().equals(dto.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "user.password.no.match");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());

        if (dto.getCurrentPassword() != null &&
                !passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            errors.rejectValue("currentPassword", "user.password.invalid");
        }

    }

}
