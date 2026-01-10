package de.othr.event_hub.dto;

import java.util.List;

import org.springframework.hateoas.EntityModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFriendshipsDTO {
    private Long id;
    
    private String username;

    private List<EntityModel<FriendshipDTO>> activeFriendships;

    private List<EntityModel<FriendshipDTO>> friendshipsRequestedTo;

    private List<EntityModel<FriendshipDTO>> friendshipsRequestedBy;
}
