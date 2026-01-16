package de.othr.event_hub.service;

import java.util.List;

import de.othr.event_hub.model.Activity;
import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.User;
import de.othr.event_hub.model.enums.ActivityType;

public interface ActivityService {
    void logActivity(User actor, Event event, ActivityType type, String message, String link);

    List<Activity> getActivityFeed(Long userId);
}
