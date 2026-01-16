package de.othr.event_hub.service;

import java.util.List;

import de.othr.event_hub.model.Notification;
import de.othr.event_hub.model.User;
import de.othr.event_hub.model.enums.NotificationType;

public interface NotificationService {
    void createNotification(User recipient, NotificationType type, String message, String link);

    List<Notification> getNotifications(Long userId);

    List<Notification> getUnreadNotifications(Long userId);

    Long getUnreadNotificationsCount(Long userId);

    void markAsRead(Long notificationId);

    void deleteNotification(Long notificationId);
}
