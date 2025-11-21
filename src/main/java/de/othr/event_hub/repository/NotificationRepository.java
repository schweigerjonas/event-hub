package de.othr.event_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.othr.event_hub.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
}
