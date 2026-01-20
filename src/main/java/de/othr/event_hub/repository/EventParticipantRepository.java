package de.othr.event_hub.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import de.othr.event_hub.model.EventParticipant;
import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.User;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    Page<EventParticipant> findByEvent(Event event, Pageable pageable);

    java.util.List<EventParticipant> findAllByEvent(Event event);

    boolean existsByEventAndUser(Event event, User user);

    long countByEvent(Event event);

    @Modifying
    // remove a single participant from an event
    void deleteByEventAndUser(Event event, User user);

    @Modifying
    // remove all participants for an event
    void deleteByEvent(Event event);
}
