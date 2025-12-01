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
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email should not be blank.")
    @Email(message = "Invalid email.")
    private String email;

    @NotBlank(message = "Username should not be blank.")
    private String username;

    @NotBlank(message = "Password should not be blank.")
    @Size(min = 5, message = "Password must have at least 5 characters.")
    private String password;

    private Integer active;

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_authorities", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private List<Authority> authorities = new ArrayList<>();
}
