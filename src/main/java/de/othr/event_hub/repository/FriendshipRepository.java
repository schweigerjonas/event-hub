package de.othr.event_hub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.othr.event_hub.model.Friendship;
import de.othr.event_hub.model.User;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    
    @Query("SELECT f FROM Friendship f WHERE f.status = 'ACCEPTED' and (f.requestor = :user OR f.addressee = :user)")
    List<Friendship> findActiveFriendshipsByUser(User user);

    @Query("SELECT f FROM Friendship f WHERE f.status = 'PENDING' and f.requestor = :user")
    List<Friendship> findPendingFriendshipsRequestedByUser(User user);

    @Query("SELECT f FROM Friendship f WHERE f.status = 'PENDING' and f.addressee = :user")
    List<Friendship> findPendingFriendshipsRequestedToUser(User user);
}
