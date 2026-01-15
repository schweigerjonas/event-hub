package de.othr.event_hub.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import de.othr.event_hub.model.Activity;
import de.othr.event_hub.model.enums.ActivityType;
import de.othr.event_hub.repository.ActivityRepository;
import de.othr.event_hub.service.ActivityService;

public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public void logActivity(Long actorId, String actorName, ActivityType type, Long eventId, String eventName) {
        Activity activity = new Activity();

        activity.setActorId(actorId);
        activity.setActorName(actorName);
        activity.setType(type);
        activity.setEventId(eventId);
        activity.setEventName(eventName);

        activityRepository.save(activity);
    }

    @Override
    public List<Activity> getActivityFeed(Long userId) {
        return activityRepository.findFriendActivities(userId);
    }
}
