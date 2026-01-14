package de.othr.event_hub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.othr.event_hub.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId);

    // notification bell dot count
    Long countByRecipientIdAndIsReadFalse(Long recipientId);
}
