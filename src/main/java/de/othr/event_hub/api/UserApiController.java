package de.othr.event_hub.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.othr.event_hub.dto.ChatMessageDTO;
import de.othr.event_hub.dto.EventApiDto;
import de.othr.event_hub.dto.UserApiDto;
import de.othr.event_hub.model.Authority;
import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.User;
import de.othr.event_hub.service.AuthorityService;
import de.othr.event_hub.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserApiController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    // CRUDs
    @PostMapping
    public ResponseEntity<UserApiDto> createUser(@RequestBody UserApiDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setActive(userDto.getActive());
        user.setPassword(encoder.encode(userDto.getPassword()));

        Authority authority = authorityService.getAuthorityByDescription(userDto.getRole());
        user.setAuthorities(new ArrayList<>(List.of(authority)));

        User savedUser = userService.saveUser(user);

        return new ResponseEntity<>(toDto(savedUser), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserApiDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        List<UserApiDto> userDtos = users.stream().map(user -> this.toDto(user)).collect(Collectors.toList());

        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserApiDto> getUserById(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toDto(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserApiDto> updateUser(@PathVariable("id") Long id, @RequestBody UserApiDto userDto) {
        User user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPassword(encoder.encode(userDto.getPassword()));

        Authority authority = authorityService.getAuthorityByDescription(userDto.getRole());

        if (authority == null) {
            return ResponseEntity.notFound().build();
        }

        user.setAuthorities(new ArrayList<>(List.of(authority)));

        user.setActive(userDto.getActive());
        user.setUsing2FA(userDto.isUsing2FA());

        User updatedUser = userService.updateUser(user);

        return ResponseEntity.ok(toDto(updatedUser));
    }

    @DeleteMapping
    public ResponseEntity<UserApiDto> deleteAllUsers() {
        List<User> users = userService.getAllUsers();

        for (User user : users) {
            user.anonymize();
            userService.saveUser(user);
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserApiDto> deleteUserById(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.anonymize();
        userService.saveUser(user);

        return ResponseEntity.noContent().build();
    }

    // Custom queries
    @GetMapping("/{id}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getUserSentMessages(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<ChatMessageDTO> messageDtos = user.getSentMessages().stream().map(message -> new ChatMessageDTO(
                message.getId(),
                message.getMessage(),
                message.getSender().getUsername(),
                user.getId(),
                message.getSentAt(),
                message.isDeleted())).collect(Collectors.toList());

        return ResponseEntity.ok(messageDtos);
    }

    @GetMapping("/{id}/favourites")
    public ResponseEntity<List<EventApiDto>> getUserEventFavourites(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<EventApiDto> favouriteEventsDtos = user.getFavourites().stream().map(favourite -> {
            Event event = favourite.getEvent();
            return new EventApiDto(
                    event.getId(),
                    event.getName(),
                    event.getLocation(),
                    event.getDurationMinutes(),
                    event.getMaxParticipants(),
                    event.getDescription(),
                    event.getEventTime(),
                    event.getCosts(),
                    event.getOrganizer().getId());
        }).collect(Collectors.toList());

        return ResponseEntity.ok(favouriteEventsDtos);
    }

    // Helpers
    private UserApiDto toDto(User user) {
        String role = (user.getAuthorities() != null && !user.getAuthorities().isEmpty())
                ? user.getAuthorities().get(0).getDescription()
                : "BENUTZER";

        return new UserApiDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getPassword(),
                role,
                user.getActive(),
                user.isUsing2FA());
    }
}
