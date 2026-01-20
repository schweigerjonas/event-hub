package de.othr.event_hub.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.othr.event_hub.model.enums.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipient_id", referencedColumnName = "id")
    @JsonIgnoreProperties({
            "sentRequests",
            "receivedRequests",
            "ownedChatRooms",
            "sentMessages",
            "chatMemberships",
            "favourites",
            "authorities",
            "password",
            "secret"
    })
    private User recipient;

    private NotificationType type;
    private String message;
    private String link; // redirect URL
    private boolean isRead = false;
    private LocalDateTime createdAt = LocalDateTime.now();
}
