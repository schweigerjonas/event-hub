package de.othr.event_hub.service;

import java.util.List;
import java.util.Optional;

import de.othr.event_hub.model.Friendship;

public interface FriendshipService {
    
    Friendship createFriendship(Friendship friendship);

    List<Friendship> getAllFriendships();

    Optional<Friendship> getFriendshipById(Long id);

    Friendship updateFriendship(Friendship friendship);

    void deleteFriendship(Friendship friendship);

    void deleteAllFriendships();
}
