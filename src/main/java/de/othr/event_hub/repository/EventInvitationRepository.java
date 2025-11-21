package de.othr.event_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.othr.event_hub.model.EventInvitation;

public interface EventInvitationRepository extends JpaRepository<EventInvitation, Long> {
    
}
