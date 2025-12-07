package de.othr.event_hub.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.othr.event_hub.model.Authority;
import de.othr.event_hub.model.User;
import de.othr.event_hub.service.AuthorityService;
import de.othr.event_hub.service.UserService;

@Controller
public class AdminDashboardController {
    private UserService userService;
    private AuthorityService authorityService;

    public AdminDashboardController(UserService userService, AuthorityService authorityService) {
        super();
        this.userService = userService;
        this.authorityService = authorityService;
    }

    @GetMapping("/admin")
    public String getAdminDashboard(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);

        return "admin/admin-dashboard";
    }

    @DeleteMapping("/admin/{id}")
    public String deleteUserFromAdminDashboard(@PathVariable String id) {
        User user = userService.getUserById(Long.valueOf(id));
        userService.deleteUser(user);

        return "redirect:/admin";
    }

    @PatchMapping("/admin/{id}")
    public String toggleUserActiveStatusFromAdminDashboard(@PathVariable String id) {
        User user = userService.getUserById(Long.valueOf(id));

        // toggle "active" status for user
        Integer active = user.getActive() == 0 ? 1 : 0;
        user.setActive(active);

        userService.updateUser(user);

        return "redirect:/admin";
    }

    @PutMapping("/admin/{id}")
    public String updateUserFromAdminDashboard(@PathVariable String id, @RequestParam Map<String, String> userDetails) {
        User user = userService.getUserById(Long.valueOf(id));
        Authority authority = authorityService.getAuthorityByDescription(userDetails.get("authority"));

        user.setEmail(userDetails.get("email"));
        user.setUsername(userDetails.get("username"));
        user.setAuthorities(new ArrayList<>(List.of(authority)));

        userService.updateUser(user);

        return "redirect:/admin";
    }
}
