package de.othr.event_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.othr.event_hub.model.ActivityFeed;

public interface ActivityFeedRepository extends JpaRepository<ActivityFeed, Long> {
    
}
