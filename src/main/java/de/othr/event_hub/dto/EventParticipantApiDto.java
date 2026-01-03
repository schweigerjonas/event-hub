package de.othr.event_hub.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipantApiDto {
    private Long id;
    private Long eventId;
    private Long userId;
    private String username;
    private boolean organizer;
    private LocalDateTime joinedAt;
}
