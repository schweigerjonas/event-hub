package de.othr.event_hub.dto;

import java.time.LocalDateTime;

import de.othr.event_hub.model.enums.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipDTO {
    private Long id;
    private Long requestorId;
    private String requestorUsername;
    private Long addresseeId;
    private String addresseeUsername;
    private FriendshipStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
}

