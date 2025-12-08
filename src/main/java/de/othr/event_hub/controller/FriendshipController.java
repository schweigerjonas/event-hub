package de.othr.event_hub.controller;

import java.time.LocalDateTime;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import de.othr.event_hub.config.AccountUserDetails;
import de.othr.event_hub.model.Friendship;
import de.othr.event_hub.model.User;
import de.othr.event_hub.model.enums.FriendshipStatus;
import de.othr.event_hub.service.FriendshipService;
import de.othr.event_hub.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
@RequestMapping("/friends")
public class FriendshipController {
    
    private FriendshipService friendshipService;
    private UserService userService;

    public FriendshipController(FriendshipService friendshipService, UserService userService) {
        super();
        this.friendshipService = friendshipService;
        this.userService = userService;
    }

    @GetMapping("/all")
    public String showFriendships(Model model, @AuthenticationPrincipal AccountUserDetails details) {
        User user = details.getUser(); 
        model.addAttribute("activeFriendships", friendshipService.findActiveFriendshipsByUser(user));
        model.addAttribute("pendingRequestsBy", friendshipService.findPendingFriendshipsRequestedByUser(user));
        model.addAttribute("pendingRequestsTo", friendshipService.findPendingFriendshipsRequestedToUser(user));
        model.addAttribute("currentUserId", user.getId());

        return "friends/friends-all";
    }

    @PostMapping("/request")
    public String sendFriendRequest(@RequestBody String username, @AuthenticationPrincipal AccountUserDetails details) {
        User currentUser = details.getUser();
        User otherUser = userService.getUserByUsername(username);
        Friendship friendship = new Friendship();
        friendship.setRequestor(currentUser);
        friendship.setAddressee(otherUser);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendship.setCreatedAt(LocalDateTime.now());
        friendshipService.createFriendship(friendship);
        return "redirect:/friends/all";
    }
    
    @GetMapping("/remove/{id}")
    public String removeFriendship(@PathVariable("id") Long id) {
        Friendship friendship = friendshipService.getFriendshipById(id).get();
        friendshipService.deleteFriendship(friendship);
        return "redirect:/friends/all";
    }

    @PostMapping("/accept/{id}")
    public String acceptFriendRequest(@PathVariable("id") Long id) {
        Friendship friendship = friendshipService.getFriendshipById(id).get();
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendship.setAcceptedAt(LocalDateTime.now());
        friendshipService.updateFriendship(friendship);
        return "redirect:/friends/all";
    }
}
