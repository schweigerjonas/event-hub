package de.othr.event_hub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.othr.event_hub.model.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Optional<Authority> findByDescription(String description);
}
