package de.othr.event_hub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.othr.event_hub.model.Event;

public interface EventService {

    List<Event> getAllEvents();

    Optional<Event> getEventById(Long id);

    Event createEvent(Event event);

    Page<Event> getEvents(String keyword, Pageable pageable);

    void deleteEvent(Event event);

    Event updateEvent(Event event);
}
