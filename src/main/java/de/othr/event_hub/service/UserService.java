package de.othr.event_hub.service;

import java.util.List;

import de.othr.event_hub.model.User;

public interface UserService {
    User saveUser(User user, String authorityDescription);

    List<User> getAllUsers();

    User getUserById(Long id);

    User updateUser(User user);

    void deleteUser(User user);

    boolean usernameExists(String username);

    boolean emailExists(String username);
}
