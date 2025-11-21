package de.othr.event_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.othr.event_hub.model.ChatMembership;

public interface ChatMembershipRepository extends JpaRepository<ChatMembership, Long> {
    
}
