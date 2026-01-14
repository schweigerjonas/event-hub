package de.othr.event_hub.model;

import java.time.LocalDateTime;

import de.othr.event_hub.model.enums.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recipientId;
    private NotificationType type;
    private String message;
    private String link; // redirect URL
    private boolean isRead = false;
    private LocalDateTime createdAt = LocalDateTime.now();
}
