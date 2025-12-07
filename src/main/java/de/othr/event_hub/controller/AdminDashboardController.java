package de.othr.event_hub.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.othr.event_hub.dto.UserDto;
import de.othr.event_hub.model.Authority;
import de.othr.event_hub.model.User;
import de.othr.event_hub.service.AuthorityService;
import de.othr.event_hub.service.UserService;
import de.othr.event_hub.util.UserMapper;
import jakarta.validation.Valid;

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
        UserDto userDto = new UserDto();
        List<User> users = userService.getAllUsers();

        userDto.setRole("BENUTZER");

        model.addAttribute("users", users);
        model.addAttribute("userDto", userDto);

        return "admin/admin-dashboard";
    }

    @PostMapping("/admin")
    public String createUserFromAdminDashboard(
            @ModelAttribute @Valid UserDto userDto,
            BindingResult result,
            RedirectAttributes attr,
            Model model) {
        if (result.hasErrors()) {
            System.out.println("Errors: " + result.getAllErrors().toString());

            // TODO: Add attributes with dto values etc and open modal again if there
            // occured any errors, ideally it should behave like this: Create User -> error
            // -> get sent to /admin -> automatically reopen create user modal -> have modal
            // input and validation errors displayed in the modal

            return "redirect:/admin";
        }

        User user = new UserMapper().toEntity(userDto);
        userService.saveUser(user, userDto.getRole());

        return "redirect:/admin";
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
