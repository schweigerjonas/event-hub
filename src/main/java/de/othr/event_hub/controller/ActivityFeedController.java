package de.othr.event_hub.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import de.othr.event_hub.config.AccountUserDetails;
import de.othr.event_hub.model.Activity;
import de.othr.event_hub.service.ActivityService;

@Controller
public class ActivityFeedController {
    private final ActivityService activityService;

    public ActivityFeedController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/feed")
    public String getActivityFeed(@AuthenticationPrincipal AccountUserDetails details, Model model) {
        if (details == null || details.getUser() == null) {
            return "redirect:/login";
        }

        List<Activity> activities = activityService.getActivityFeed(details.getUser().getId());

        model.addAttribute("activities", activities);

        return "activities/feed";
    }

}
