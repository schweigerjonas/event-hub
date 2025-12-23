package de.othr.event_hub.service;

import java.util.Optional;

import de.othr.event_hub.model.Event;

public interface EventService {

    Optional<Event> getEventById(Long id);
}
