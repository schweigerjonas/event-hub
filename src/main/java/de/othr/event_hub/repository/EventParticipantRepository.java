package de.othr.event_hub.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import de.othr.event_hub.model.EventParticipant;
import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.User;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    Page<EventParticipant> findByEvent(Event event, Pageable pageable);

    boolean existsByEventAndUser(Event event, User user);

    long countByEvent(Event event);

    void deleteByEventAndUser(Event event, User user);
}
