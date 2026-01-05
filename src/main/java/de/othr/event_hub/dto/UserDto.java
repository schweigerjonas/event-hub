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
    @NotBlank(message = "Bitte geben Sie Ihre Email-Adresse ein")
    @Email(message = "Bitte geben Sie eine gültige Email-Adresse ein")
    private String email;

    @NotBlank(message = "Bitte geben Sie einen Benutzernamen ein")
    @Size(min = 3, max = 50, message = "Der Benutzername muss zwischen 3 und 50 Zeichen lang sein")
    private String username;

    @NotBlank(message = "Bitte geben Sie ein Passwort ein")
    @Size(min = 8, message = "Das Passwort muss mindestens 8 Zeichen lang sein")
    private String password;

    @NotBlank(message = "Bitte bestätigen Sie Ihr Passwort")
    @Size(min = 8, message = "Das Bestätigungspasswort muss mindestens 8 Zeichen lang sein")
    private String confirmPassword;

    @NotBlank(message = "Bitte wählen Sie eine Rolle aus")
    private String role;

    private boolean using2FA;
}
