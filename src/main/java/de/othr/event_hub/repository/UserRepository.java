package de.othr.event_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.othr.event_hub.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
}
