package de.othr.event_hub.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import de.othr.event_hub.model.Authority;
import de.othr.event_hub.model.User;
import de.othr.event_hub.model.enums.OAuthProvider;
import de.othr.event_hub.repository.AuthorityRepository;
import de.othr.event_hub.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    public OAuth2LoginSuccessHandler(UserRepository userRepository, AuthorityRepository authorityRepository, OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        AccountUserDetails userDetails = new AccountUserDetails(createUserFromAuthentication(authentication));

        UsernamePasswordAuthenticationToken newAuth =
            new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

        SecurityContextHolder.getContext().setAuthentication(newAuth);

        response.sendRedirect("/");
    }

    private User createUserFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2User oAuth2User && authentication instanceof OAuth2AuthenticationToken oauthToken) {
            String providerName = oauthToken.getAuthorizedClientRegistrationId();
            String providerId = extractProviderId(oAuth2User, providerName);
            String email = oAuth2User.getAttribute("email");
            if ("github".equals(providerName) && email == null) {
                email = fetchGithubEmail(oauthToken);
            }
            String username = email.split("@")[0];
            if ("github".equals(providerName)) {
                username = oAuth2User.getAttribute("login");
            }

            // check if user with this email already exists. If not, create new user
            Optional<User> existingUserOpt = userRepository.findUserByUsername(username);
            if (existingUserOpt.isPresent()) {
                User existingUser = existingUserOpt.get();
                // update user data
                existingUser.setUsername(username);
                existingUser.setEmail(email);
                if (providerName.equals("google")) {
                    existingUser.setAuthProvider(OAuthProvider.GOOGLE);
                } else if (providerName.equals("github")) {
                    existingUser.setAuthProvider(OAuthProvider.GITHUB);
                }
                existingUser.setProviderId(providerId);
                userRepository.save(existingUser);
                return existingUser;
            }
            User user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setPassword(null);
            user.setActive(1);
            if (providerName.equals("google")) {
                user.setAuthProvider(OAuthProvider.GOOGLE);
            } else if (providerName.equals("github")) {
                user.setAuthProvider(OAuthProvider.GITHUB);
            }
            user.setProviderId(providerId);
            Authority authority = authorityRepository.findAuthorityByDescription("BENUTZER").get();
            user.setAuthorities(new ArrayList<>(List.of(authority)));
            userRepository.save(user);
            return user;
        }

        return null;
    }

    private String extractProviderId(OAuth2User oauthUser, String provider) {
        if ("google".equals(provider)) {
            return oauthUser.getAttribute("sub");
        }
        if ("github".equals(provider)) {
            Object id = oauthUser.getAttribute("id");
            return id != null ? id.toString() : null;
        }

        throw new IllegalArgumentException("Unsupported provider: " + provider);
    }

    // fetch github email: AI generated
    private String fetchGithubEmail(OAuth2AuthenticationToken oauthToken) {
        var client = oAuth2AuthorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );

        if (client == null || client.getAccessToken() == null) {
            return null;
        }

        String accessToken = client.getAccessToken().getTokenValue();

        try {
            var restTemplate = new org.springframework.web.client.RestTemplate();
            var headers = new org.springframework.http.HttpHeaders();
            headers.setBearerAuth(accessToken);
            var entity = new org.springframework.http.HttpEntity<Void>(headers);

            var response = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    org.springframework.http.HttpMethod.GET,
                    entity,
                    java.util.List.class
            );

            var emails = response.getBody();
            if (emails != null) {
                for (Object obj : emails) {
                    if (obj instanceof java.util.Map<?, ?> map) {
                        Boolean primary = (Boolean) map.get("primary");
                        Boolean verified = (Boolean) map.get("verified");
                        if (Boolean.TRUE.equals(primary) && Boolean.TRUE.equals(verified)) {
                            Object emailObj = map.get("email");
                            if (emailObj != null) return emailObj.toString();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

