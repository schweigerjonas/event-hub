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
public class UpdateUserInfoDto {
    @NotBlank(message = "Bitte geben Sie Ihre alte Email-Adresse ein")
    @Email(message = "Bitte geben Sie eine gültige Email-Adresse ein")
    private String email;

    @NotBlank(message = "Bitte geben Sie Ihren alten Benutzernamen ein")
    @Size(min = 3, max = 50, message = "Der Benutzername muss zwischen 3 und 50 Zeichen lang sein")
    private String username;
}
