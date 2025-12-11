package de.othr.event_hub.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import de.othr.event_hub.model.enums.ChatRoomType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ChatRoomType type;

    private String name; // name is only needed for group chats

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event; // event is only needed for event chats

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner; // for event chats: owner = event organisator

    @OneToMany(mappedBy = "chatRoom")
    private Set<ChatMembership> members = new HashSet<>();

    @OneToMany(mappedBy = "chatRoom")
    private Set<ChatMessage> messages = new HashSet<>();
}
