package de.othr.event_hub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.othr.event_hub.model.Activity;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("""
            SELECT a FROM Activity a
            WHERE a.actor.id IN (
                SELECT CASE
                    WHEN f.requestor.id = :userId THEN f.addressee.id
                    ELSE f.requestor.id
                END
                FROM Friendship f
                WHERE (f.requestor.id = :userId or f.addressee.id = :userId)
                AND f.status = de.othr.event_hub.model.enums.FriendshipStatus.ACCEPTED
            )
            ORDER BY a.timestamp DESC
            """)
    List<Activity> findFriendActivities(@Param("userId") Long userId);
}
