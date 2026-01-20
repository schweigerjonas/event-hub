package de.othr.event_hub.config;

import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import de.othr.event_hub.model.User;
import de.othr.event_hub.repository.UserRepository;

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String verificationCode = ((CustomWebAuthenticationDetails) auth.getDetails()).getVerificationCode();
        User user = userRepository.findUserByUsername(auth.getName())
                .orElseThrow(() -> new BadCredentialsException("Ungültiger Benutzername oder Passwort"));

        if (user.isUsing2FA()) {
            Totp totp = new Totp(user.getSecret());

            if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
                throw new BadCredentialsException("Ungültiger 2FA Code");
            }
        }

        return super.authenticate(auth);
    }

    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}