package de.othr.event_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.othr.event_hub.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    
}
