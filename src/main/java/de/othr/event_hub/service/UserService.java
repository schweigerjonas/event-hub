package de.othr.event_hub.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.othr.event_hub.dto.UpdateUserInfoDto;
import de.othr.event_hub.model.User;

public interface UserService {
    User saveUser(User user, String authorityDescription);

    User saveUser(User user);

    List<User> getAllUsers();

    User getUserById(Long id);

    User updateUser(User user);

    void deleteUser(User user);

    User getUserByUsername(String username);

    boolean usernameExists(String username);

    boolean emailExists(String username);

    Page<User> getAllUsers(String username, Pageable pageable);

    String generateQRUrl(User user);

    User softDeleteUserByUsername(String username);

    void updatePassword(String username, String newPassword);

    void updateUserAuthority(String username, String authorityDescription);

    void updateUserInfo(String username, UpdateUserInfoDto userDto);

    void deleteAllUsers();
}
