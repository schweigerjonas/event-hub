package de.othr.event_hub.controller;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.othr.event_hub.api.jwt.AuthenticationRequest;
import de.othr.event_hub.api.jwt.AuthenticationResponse;
import de.othr.event_hub.api.jwt.RegisterRequest;
import de.othr.event_hub.model.Authority;
import de.othr.event_hub.model.User;
import de.othr.event_hub.repository.AuthorityRepository;
import de.othr.event_hub.repository.UserRepository;
import de.othr.event_hub.service.AccountUserDetailsService;
import de.othr.event_hub.service.JwtService;

@RestController
@RequestMapping("/api")
public class AuthenticationController {
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AccountUserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest registerRequest){
        User user = new User();
        user.setActive(1);
        user.setEmail(registerRequest.getEmail());
        user.setUsername(registerRequest.getUsername());
        user.setAuthorities(new ArrayList<Authority> ());

        for (int i = 0; i< registerRequest.getAuthorities().size(); i++) {
            Optional<Authority> authorityOp = authorityRepository.findById(registerRequest.getAuthorities().get(i).getId());
            user.getAuthorities().add(authorityOp.get());
        }

        user.setPassword(encoder.encode(registerRequest.getPassword()));
        user = userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername()); 
        String jwt = jwtService.generateToken(userDetails); 
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/users/authenticate")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).build();
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        String jwt = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
