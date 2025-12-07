package de.othr.event_hub.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import de.othr.event_hub.model.User;
import de.othr.event_hub.service.UserService;

@Controller
public class AdminController {
    private UserService userService;

    public AdminController(UserService userService) {
        super();
        this.userService = userService;
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
}
