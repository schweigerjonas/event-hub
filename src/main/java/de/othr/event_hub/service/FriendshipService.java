package de.othr.event_hub.service;

import java.util.List;
import java.util.Optional;

import de.othr.event_hub.model.Friendship;
import de.othr.event_hub.model.User;

public interface FriendshipService {
    
    Friendship createFriendship(Friendship friendship);

    List<Friendship> getAllFriendships();

    Optional<Friendship> getFriendshipById(Long id);

    Friendship updateFriendship(Friendship friendship);

    void deleteFriendship(Friendship friendship);

    void deleteAllFriendships();

    public List<Friendship> findActiveFriendshipsByUser(User user);

    public List<Friendship> findPendingFriendshipsRequestedByUser(User user);

    public List<Friendship> findPendingFriendshipsRequestedToUser(User user);

    public List<Friendship> findAllActiveFriendships();

    public List<Friendship> findAllPendingFriendships();
    
    public boolean existsFriendshipBetween(User current, User other);
}
