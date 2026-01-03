package de.othr.event_hub.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.othr.event_hub.model.EventInvitation;
import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.User;
import de.othr.event_hub.model.enums.EventInvitationStatus;

public interface EventInvitationRepository extends JpaRepository<EventInvitation, Long> {
    Optional<EventInvitation> findByEventAndInvitee(Event event, User invitee);

    boolean existsByEventAndInviteeAndStatus(Event event, User invitee, EventInvitationStatus status);

    Page<EventInvitation> findByInvitee(User invitee, Pageable pageable);

    Page<EventInvitation> findByInviter(User inviter, Pageable pageable);

    @Query("SELECT i FROM EventInvitation i WHERE i.invitee = :user AND LOWER(i.event.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<EventInvitation> searchIncoming(@Param("user") User user, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT i FROM EventInvitation i WHERE i.inviter = :user AND LOWER(i.event.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<EventInvitation> searchOutgoing(@Param("user") User user, @Param("keyword") String keyword, Pageable pageable);
}
