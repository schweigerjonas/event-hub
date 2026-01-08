package de.othr.event_hub.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

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

        Authority authority = authorityRepository.findAuthorityByDescription(authorityDescription)
                .orElseThrow(() -> new RuntimeException("Authority not found: " + authorityDescription));
        user.setAuthorities(new ArrayList<>(List.of(authority)));

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

    public boolean usernameExists(String username) {
        return userRepository.findUserByUsername(username).isPresent();
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.findUserByEmail(email).isPresent();
    }

    @Override
    public Page<User> getAllUsers(String username, Pageable pageable) {
        Page<User> pageUsers;

        if (username == null) {
            pageUsers = userRepository.findAll(pageable);
        } else {
            pageUsers = userRepository.findByUsernameContainingIgnoreCase(username, pageable);
        }

        return pageUsers;
    }

    @Override
    public String generateQRUrl(User user) {
        String APP_NAME = "EventHub";
        String otpAuthUrl = String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                APP_NAME, user.getEmail(), user.getSecret(), APP_NAME);

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthUrl, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngData);
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR Url", e);
        }
    }

    @Override
    @Transactional
    public User softDeleteUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.anonymize();
        return userRepository.save(user);
    }

    @Override
    public void updatePassword(String username, String newPassword) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
