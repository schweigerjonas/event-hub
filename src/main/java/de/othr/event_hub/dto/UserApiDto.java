package de.othr.event_hub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserApiDto {
    private Long id;
    private String email;
    private String username;
    private String role;
    private Integer active;
    private boolean using2FA;
}
