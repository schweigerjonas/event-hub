package de.othr.event_hub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    @GetMapping("/admin")
    public String getAdminDashboard() {
        return "admin/admin-dashboard";
    }

}
