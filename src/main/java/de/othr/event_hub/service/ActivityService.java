package de.othr.event_hub.service;

import java.util.List;

import de.othr.event_hub.model.Activity;
import de.othr.event_hub.model.User;
import de.othr.event_hub.model.enums.ActivityType;

public interface ActivityService {
    void logActivity(User actor, Long eventId, ActivityType type, String message, String link);

    List<Activity> getActivityFeed(Long userId);
}
