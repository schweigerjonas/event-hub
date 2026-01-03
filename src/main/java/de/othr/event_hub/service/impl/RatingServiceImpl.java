package de.othr.event_hub.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.Rating;
import de.othr.event_hub.model.User;
import de.othr.event_hub.repository.RatingRepository;
import de.othr.event_hub.service.RatingService;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Override
    public Rating createRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    @Override
    public Rating updateRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    @Override
    public void deleteRating(Rating rating) {
        ratingRepository.delete(rating);
    }

    @Override
    public Optional<Rating> getRatingByEventAndUser(Event event, User user) {
        return ratingRepository.findByEventAndUser(event, user);
    }

    @Override
    public Page<Rating> getRatings(Event event, String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return ratingRepository.findByEvent(event, pageable);
        }
        return ratingRepository.findByEventAndCommentContainingIgnoreCase(event, keyword, pageable);
    }

    @Override
    public long countRatings(Event event) {
        return ratingRepository.countByEvent(event);
    }

    @Override
    public Double getAverageRating(Event event) {
        return ratingRepository.getAverageByEvent(event);
    }
}
