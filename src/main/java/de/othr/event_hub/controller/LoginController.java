package de.othr.event_hub.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.othr.event_hub.config.AccountUserDetails;
import de.othr.event_hub.model.Authority;
import de.othr.event_hub.model.User;
import de.othr.event_hub.service.AuthorityService;
import de.othr.event_hub.service.UserService;

@Controller
public class LoginController {

    private AuthorityService authorityService;
    private UserService userService;

    public LoginController(AuthorityService authorityService, UserService userService) {
        super();
        this.authorityService = authorityService;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("/oauth/role")
    public String getRoleSelect() {
        return "role-select";
    }

    @PostMapping("/roleassign")
    public String assignRole(@RequestParam String role, @AuthenticationPrincipal AccountUserDetails accountUserDetails) {
        User user = accountUserDetails.getUser();
        Authority authority = authorityService.getAuthorityByDescription(role);
        user.setAuthorities(new ArrayList<>(List.of(authority)));
        userService.updateUser(user);

        // update security context
        AccountUserDetails updatedDetails = new AccountUserDetails(user);
        UsernamePasswordAuthenticationToken newAuth =
                new UsernamePasswordAuthenticationToken(
                        updatedDetails,
                        null,
                        updatedDetails.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return "redirect:/";
    }
}
