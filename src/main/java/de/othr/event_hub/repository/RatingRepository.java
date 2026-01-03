package de.othr.event_hub.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.othr.event_hub.model.Rating;
import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.User;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByEventAndUser(Event event, User user);

    Page<Rating> findByEvent(Event event, Pageable pageable);

    Page<Rating> findByEventAndCommentContainingIgnoreCase(Event event, String comment, Pageable pageable);

    long countByEvent(Event event);

    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.event = :event")
    Double getAverageByEvent(@Param("event") Event event);
}
