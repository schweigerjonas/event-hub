package de.othr.event_hub.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.othr.event_hub.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    Page<User> findAll(Pageable pageable);

    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}
