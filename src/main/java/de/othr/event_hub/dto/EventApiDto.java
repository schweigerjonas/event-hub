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
public class EventApiDto { // api payload for events
    private Long id;
    private String name;
    private String location;
    private Integer durationMinutes;
    private Integer maxParticipants;
    private String description;
    private LocalDateTime eventTime;
    private double costs;
    private Long organizerId;
}
