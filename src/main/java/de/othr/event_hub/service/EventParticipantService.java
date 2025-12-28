package de.othr.event_hub.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.EventParticipant;
import de.othr.event_hub.model.User;

public interface EventParticipantService {

    EventParticipant createParticipant(EventParticipant participant);

    Page<EventParticipant> getParticipants(Event event, Pageable pageable);

    boolean existsParticipant(Event event, User user);

    long countParticipants(Event event);

    void deleteParticipant(Event event, User user);
}
