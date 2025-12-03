package de.othr.event_hub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 50, message = "Username should have between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    private String confirmPassword;

    @NotBlank(message = "Role must not be blank")
    private String role;
}
