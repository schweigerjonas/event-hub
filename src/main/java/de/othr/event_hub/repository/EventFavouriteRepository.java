package de.othr.event_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.EventFavourite;
import de.othr.event_hub.model.User;

public interface EventFavouriteRepository extends JpaRepository<EventFavourite, Long> {
    
    EventFavourite findByEventAndUser(Event event, User user);
}
