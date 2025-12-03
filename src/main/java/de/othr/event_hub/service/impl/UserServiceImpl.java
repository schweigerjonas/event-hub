package de.othr.event_hub.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import de.othr.event_hub.model.User;
import de.othr.event_hub.repository.UserRepository;
import de.othr.event_hub.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }

    @Override
    public User saveUser(User user) {
        // TODO Auto-generated method stub
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
}
