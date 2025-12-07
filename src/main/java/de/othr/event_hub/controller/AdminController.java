package de.othr.event_hub.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

}
