package de.othr.event_hub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.othr.event_hub.model.ChatMembership;
import de.othr.event_hub.model.User;

public interface ChatMembershipRepository extends JpaRepository<ChatMembership, Long> {

    List<ChatMembership> findByUser(User user);
}
