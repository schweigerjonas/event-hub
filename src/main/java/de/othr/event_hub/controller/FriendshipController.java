package de.othr.event_hub.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.othr.event_hub.config.AccountUserDetails;
import de.othr.event_hub.model.Authority;
import de.othr.event_hub.model.Friendship;
import de.othr.event_hub.model.User;
import de.othr.event_hub.model.enums.FriendshipStatus;
import de.othr.event_hub.model.enums.NotificationType;
import de.othr.event_hub.service.FriendshipService;
import de.othr.event_hub.service.NotificationService;
import de.othr.event_hub.service.UserService;

@Controller
@RequestMapping("/friends")
public class FriendshipController {

    private FriendshipService friendshipService;
    private UserService userService;
    private NotificationService notificationService;

    public FriendshipController(FriendshipService friendshipService, UserService userService,
            NotificationService notificationService) {
        super();
        this.friendshipService = friendshipService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/all")
    public String showFriendships(Model model, @AuthenticationPrincipal AccountUserDetails details) {
        User user = details.getUser();
        List<Authority> userAuthorities = user.getAuthorities();
        List<String> authorityDescriptions = userAuthorities.stream().map(Authority::getDescription).toList();
        if (authorityDescriptions.contains("ADMIN")) {
            model.addAttribute("activeFriendships", friendshipService.findAllActiveFriendships());
            model.addAttribute("pendingFriendships", friendshipService.findAllPendingFriendships());
        } else {
            model.addAttribute("activeFriendships", friendshipService.findActiveFriendshipsByUser(user));
            model.addAttribute("pendingRequestsBy", friendshipService.findPendingFriendshipsRequestedByUser(user));
            model.addAttribute("pendingRequestsTo", friendshipService.findPendingFriendshipsRequestedToUser(user));
            model.addAttribute("currentUserId", user.getId());
        }

        return "friends/friends-all";
    }

    @PostMapping("/request")
    public String sendFriendRequest(@RequestParam String name, @AuthenticationPrincipal AccountUserDetails details,
            RedirectAttributes redirectAttributes) {
        User currentUser = details.getUser();
        User otherUser = userService.getUserByUsername(name);
        if (otherUser == null) {
            redirectAttributes.addFlashAttribute("error", "Der gesuchte Benutzer existiert nicht.");
            return "redirect:/friends/all";
        }
        if (otherUser.equals(currentUser)) {
            redirectAttributes.addFlashAttribute("error", "Du kannst nicht mit dir selbst befreundet sein.");
            return "redirect:/friends/all";
        }
        // check if request is allowed (no current active friendship or pending request)
        if (friendshipService.existsFriendshipBetween(currentUser, otherUser)) {
            redirectAttributes.addFlashAttribute("error",
                    "Du bist bereits mit diesem Benutzer befreundet oder es wurde bereits eine Freundschaftsanfrage gesendet.");
            return "redirect:/friends/all";
        }

        Friendship friendship = new Friendship();
        friendship.setRequestor(currentUser);
        friendship.setAddressee(otherUser);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendship.setCreatedAt(LocalDateTime.now());
        friendshipService.createFriendship(friendship);

        String message = currentUser.getUsername() + " hat dir eine Freundschaftsanfrage gesendet.";
        String link = "friends/all";

        notificationService.createNotification(otherUser.getId(), NotificationType.FRIEND_REQUEST, message, link);

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

        String message = friendship.getAddressee().getUsername() + " hat deine Freundschaftsanfrage angenommen.";
        String link = "friends/all";

        notificationService.createNotification(friendship.getRequestor().getId(),
                NotificationType.FRIEND_REQUEST_ACCEPTED,
                message, link);
        return "redirect:/friends/all";
    }
}
