package de.othr.event_hub.api.security;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import de.othr.event_hub.dto.FriendshipDTO;
import de.othr.event_hub.model.Friendship;
import de.othr.event_hub.model.User;
import de.othr.event_hub.model.enums.FriendshipStatus;
import de.othr.event_hub.service.FriendshipService;
import de.othr.event_hub.service.UserService;

@Component("friendshipSecurity")
public class FriendshipSecurity {

    private final FriendshipService friendshipService;
    private final UserService userService;

    public FriendshipSecurity(FriendshipService friendshipService, UserService userService) {
        this.friendshipService = friendshipService;
        this.userService = userService;
    }

    public boolean canAccessFriendship(Long friendshipId, Authentication authentication) {
        Optional<Friendship> friendshipOpt = friendshipService.getFriendshipById(friendshipId);
        if (!friendshipOpt.isPresent()) return false;

        Friendship friendship = friendshipOpt.get();
        String username = authentication.getName(); 
        
        if (authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .anyMatch(auth -> "ADMIN".equals(auth))) {
            return true;
        }

        return friendship.getRequestor().getUsername().equals(username) || friendship.getAddressee().getUsername().equals(username);
    }

    public boolean isCurrentUser(Long userId, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username); 

        if (authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .anyMatch(auth -> "ADMIN".equals(auth))) {
            return true;
        }

        return userId.equals(user.getId());
    }

    public boolean canCreateFriendship(FriendshipDTO friendship, Authentication authentication) {
        if (authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .anyMatch(auth -> "ADMIN".equals(auth))) {
            return true;
        }

        // user is only allowed to create a pending friendship with himself as requestor
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        if (friendship.getRequestorId().equals(user.getId()) && friendship.getStatus().equals(FriendshipStatus.PENDING) && friendship.getAcceptedAt() == null) {
            return true;
        }

        return false;
    }

    public boolean canUpdateFriendship(Long id, FriendshipDTO friendship, Authentication authentication) {
        if (authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .anyMatch(auth -> "ADMIN".equals(auth))) {
            return true;
        }

        Optional<Friendship> friendshipOpt = friendshipService.getFriendshipById(id);
        if (!friendshipOpt.isPresent()) return false;

        Friendship friendshipObject = friendshipOpt.get();

        // user is only allowed to accept a pending friendship with himself as addressee
        String username = authentication.getName();
        if (friendshipObject.getAddressee().getUsername().equals(username) && 
            friendshipObject.getStatus().equals(FriendshipStatus.PENDING) &&
            friendship.getStatus().equals(FriendshipStatus.ACCEPTED) &&
            friendship.getRequestorId().equals(friendshipObject.getRequestor().getId()) &&
            friendship.getAddresseeId().equals(friendshipObject.getAddressee().getId()) &&
            friendship.getCreatedAt().equals(friendshipObject.getCreatedAt())) {
            return true;
        }

        return false;
    }
}
