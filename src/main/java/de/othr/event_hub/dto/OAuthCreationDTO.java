package de.othr.event_hub.dto;

import de.othr.event_hub.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthCreationDTO {
    
    private User user;

    private boolean hasAlreadyExisted;
}
