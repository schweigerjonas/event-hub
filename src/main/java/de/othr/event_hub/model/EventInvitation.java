package de.othr.event_hub.model;

import java.time.LocalDateTime;

import de.othr.event_hub.model.enums.EventInvitationStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "event_invitations")
public class EventInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "inviter_id", referencedColumnName = "id")
    private User inviter;

    @ManyToOne
    @JoinColumn(name = "invitee_id", referencedColumnName = "id")
    private User invitee;

    @Enumerated(EnumType.STRING)
    private EventInvitationStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();

    // set when invitee responds
    private LocalDateTime respondedAt;
}
