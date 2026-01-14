package de.othr.event_hub.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.othr.event_hub.model.Notification;
import de.othr.event_hub.model.enums.NotificationType;
import de.othr.event_hub.repository.NotificationRepository;
import de.othr.event_hub.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void createNotification(Long recipientId, NotificationType type, String message, String link) {
        Notification notification = new Notification();

        notification.setRecipientId(recipientId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setLink(link);

        notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }
}
