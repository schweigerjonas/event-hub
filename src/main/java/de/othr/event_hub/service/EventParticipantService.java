package de.othr.event_hub.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.EventParticipant;
import de.othr.event_hub.model.User;

public interface EventParticipantService { // participant business operations

    EventParticipant createParticipant(EventParticipant participant);

    Page<EventParticipant> getParticipants(Event event, Pageable pageable);

    java.util.List<EventParticipant> getAllParticipants(Event event);

    boolean existsParticipant(Event event, User user);

    long countParticipants(Event event);

    void deleteParticipant(Event event, User user);

    void deleteParticipants(Event event);
}
