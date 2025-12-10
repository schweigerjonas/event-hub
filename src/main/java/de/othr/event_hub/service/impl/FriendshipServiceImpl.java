package de.othr.event_hub.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.othr.event_hub.model.Friendship;
import de.othr.event_hub.model.User;
import de.othr.event_hub.repository.FriendshipRepository;
import de.othr.event_hub.service.FriendshipService;

@Service
public class FriendshipServiceImpl implements FriendshipService {
    
    @Autowired
    private FriendshipRepository friendshipRepository;

    @Override
    public Friendship createFriendship(Friendship friendship) {
        return friendshipRepository.save(friendship);
    }

    @Override
    public List<Friendship> getAllFriendships() {
        return friendshipRepository.findAll();
    }

    @Override
    public Optional<Friendship> getFriendshipById(Long id) {
        return friendshipRepository.findById(id);
    }

    @Override
    public Friendship updateFriendship(Friendship friendship) {
        return friendshipRepository.save(friendship);
    }

    @Override
    public void deleteFriendship(Friendship friendship) {
        friendshipRepository.delete(friendship);
    }

    @Override
    public void deleteAllFriendships() {
        friendshipRepository.deleteAll();
    }

    @Override
    public List<Friendship> findActiveFriendshipsByUser(User user) {
        return friendshipRepository.findActiveFriendshipsByUser(user);
    }

    @Override
    public List<Friendship> findPendingFriendshipsRequestedByUser(User user) {
        return friendshipRepository.findPendingFriendshipsRequestedByUser(user);
    }

    @Override
    public List<Friendship> findPendingFriendshipsRequestedToUser(User user) {
        return friendshipRepository.findPendingFriendshipsRequestedToUser(user);
    }

    @Override
    public boolean existsFriendshipBetween(User current, User other) {
        return friendshipRepository.existsFriendshipBetween(current, other);
    }
}
