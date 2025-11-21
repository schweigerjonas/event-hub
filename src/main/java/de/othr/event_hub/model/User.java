package de.othr.event_hub.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User { // for Spring Security @Jonas

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "requestor")
    private Set<Friendship> sentRequests = new HashSet<>();

    @OneToMany(mappedBy = "addressee")
    private Set<Friendship> receivedRequests = new HashSet<>();

    @OneToMany(mappedBy = "owner")
    private Set<ChatRoom> ownedChatRooms = new HashSet<>();

    @OneToMany(mappedBy = "sender")
    private Set<ChatMessage> sentMessages = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<ChatMembership> chatMemberships = new HashSet<>();

    @ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
		name="userauthority",
		joinColumns = @JoinColumn(name="iduser"),
		inverseJoinColumns = @JoinColumn(name="idauthority")
	)
    private List<Authority> eventAuthorities = new ArrayList<>();
}
