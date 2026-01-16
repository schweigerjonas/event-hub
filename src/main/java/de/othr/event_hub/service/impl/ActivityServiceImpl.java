package de.othr.event_hub.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.othr.event_hub.model.Activity;
import de.othr.event_hub.model.enums.ActivityType;
import de.othr.event_hub.repository.ActivityRepository;
import de.othr.event_hub.service.ActivityService;

@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public void logActivity(Long actorId, Long eventId, ActivityType type, String message, String link) {
        Activity activity = new Activity();

        activity.setActorId(actorId);
        activity.setEventId(eventId);
        activity.setType(type);
        activity.setMessage(message);
        activity.setLink(link);

        activityRepository.save(activity);
    }

    @Override
    public List<Activity> getActivityFeed(Long userId) {
        return activityRepository.findFriendActivities(userId);
    }
}
