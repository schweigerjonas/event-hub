package de.othr.event_hub.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.jboss.aerogear.security.otp.api.Base32;

import de.othr.event_hub.model.enums.OAuthProvider;
import jakarta.persistence.CascadeType;
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
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Bitte geben Sie Ihre Email-Adresse ein")
    @Email(message = "Bitte geben Sie eine gültige Email-Adresse ein")
    private String email;

    @NotBlank(message = "Bitte geben Sie einen Benutzernamen ein")
    @Size(min = 3, max = 50, message = "Der Benutzername muss zwischen 3 und 50 Zeichen lang sein")
    private String username;

    // @NotBlank(message = "Bitte geben Sie ein Passwort ein")
    @Size(min = 8, message = "Das Passwort muss mindestens 8 Zeichen lang sein")
    private String password;

    private OAuthProvider authProvider = OAuthProvider.LOCAL;
    private String providerId = null;

    private Integer active;

    private boolean isUsing2FA;
    private String secret;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventFavourite> favourites = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_authorities", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private List<Authority> authorities = new ArrayList<>();

    public User() {
        super();
        this.secret = Base32.random();
    }

    @Override
    public String toString() {
        return this.username;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (getClass() != o.getClass()) {
            return false;
        }

        final User other = (User) o;
        if (!Objects.equals(id, other.id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return 0;
        } else {
            return id.hashCode();
        }
    }

    public void anonymize() {
        this.active = 0;
        this.email = "deleted_" + this.id + "@event-hub.com";
        this.username = "[deleted" + this.id + "]";
        this.password = "DELETED_ACCOUNT_PASSWORD";
        this.isUsing2FA = false;
        this.secret = null;
        this.authorities.clear();
    }
}
