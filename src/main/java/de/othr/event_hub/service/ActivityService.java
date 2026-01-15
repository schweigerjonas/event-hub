package de.othr.event_hub.service;

import java.util.List;

import de.othr.event_hub.model.Activity;
import de.othr.event_hub.model.enums.ActivityType;

public interface ActivityService {
    void logActivity(Long actorId, String actorName, ActivityType type, Long eventId, String eventName);

    List<Activity> getActivityFeed(Long userId);
}
