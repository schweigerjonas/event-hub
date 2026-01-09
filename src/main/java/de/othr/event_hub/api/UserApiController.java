package de.othr.event_hub.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.othr.event_hub.dto.UserApiDto;
import de.othr.event_hub.model.User;
import de.othr.event_hub.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserApiController {
    @Autowired
    private UserService userService;

    // CRUDs
    @GetMapping
    public ResponseEntity<List<UserApiDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        List<UserApiDto> userDtos = users.stream().map(user -> this.toDto(user)).collect(Collectors.toList());

        return ResponseEntity.ok(userDtos);
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
                role,
                user.getActive(),
                user.isUsing2FA());
    }
}
