package de.othr.event_hub.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.othr.event_hub.model.Authority;
import de.othr.event_hub.model.User;
import de.othr.event_hub.repository.AuthorityRepository;
import de.othr.event_hub.repository.UserRepository;
import de.othr.event_hub.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, AuthorityRepository authorityRepository,
            PasswordEncoder passwordEncoder) {
        super();
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(User user, String authorityDescription) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        Authority authority = authorityRepository.findByDescription(authorityDescription)
                .orElseThrow(() -> new RuntimeException("Authority not found: " + authorityDescription));
        user.setAuthorities(List.of(authority));

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        // TODO Auto-generated method stub
        return (List<User>) userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        // TODO Auto-generated method stub
        return userRepository.findById(id).get();
    }

    @Override
    public User updateUser(User user) {
        // TODO Auto-generated method stub
        // TODO update user functionality
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        // TODO Auto-generated method stub
        userRepository.delete(user);
    }

    @Override
    public User getUserByUsername(String username) {
        // TODO Auto-generated method stub
        Optional<User> user = userRepository.findUserByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            return null;
        }
    }
}
