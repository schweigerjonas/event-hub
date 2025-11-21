package de.othr.event_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.othr.event_hub.model.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    
}
