package de.othr.event_hub.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.Rating;
import de.othr.event_hub.model.User;

public interface RatingService {

    Rating createRating(Rating rating);

    Rating updateRating(Rating rating);

    void deleteRating(Rating rating);

    Optional<Rating> getRatingByEventAndUser(Event event, User user);

    Page<Rating> getRatings(Event event, String keyword, Pageable pageable);

    long countRatings(Event event);

    Double getAverageRating(Event event);

    void deleteRatingsByEvent(Event event);
}
