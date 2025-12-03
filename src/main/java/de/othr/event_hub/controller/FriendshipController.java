package de.othr.event_hub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import de.othr.event_hub.model.User;
import de.othr.event_hub.service.FriendshipService;

@Controller
@RequestMapping("/friends")
public class FriendshipController {
    
    private FriendshipService friendshipService;

    // work with a static user until User is implemented
    private User user;

    public FriendshipController(FriendshipService friendshipService) {
        super();
        this.friendshipService = friendshipService;
        user = new User();
        user.setId(1L);
    }

    @GetMapping("/all")
    public String showFriendships(Model model) {
        model.addAttribute("activeFriendships", friendshipService.findActiveFriendshipsByUser(user));
        model.addAttribute("pendingRequestsBy", friendshipService.findPendingFriendshipsRequestedByUser(user));
        model.addAttribute("pendingRequestsTo", friendshipService.findPendingFriendshipsRequestedToUser(user));
        model.addAttribute("currentUserId", user.getId());

        return "friends/friends-all";
    }
}
