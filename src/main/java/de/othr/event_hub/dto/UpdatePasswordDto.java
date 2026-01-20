package de.othr.event_hub.dto;

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
public class UpdatePasswordDto {
    @NotBlank(message = "Bitte geben Sie ein Passwort ein")
    private String currentPassword;

    @NotBlank(message = "Bitte geben Sie ein neues Passwort ein")
    @Size(min = 8, message = "Das neues Password muss mindesten 8 Zeichen lang sein")
    private String newPassword;

    @NotBlank(message = "Bitte bestätigen Sie Ihr Passwort")
    private String confirmPassword;
}
