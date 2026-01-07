package de.othr.event_hub.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.othr.event_hub.dto.UserDto;
import de.othr.event_hub.model.User;
import de.othr.event_hub.service.UserService;

@Controller
public class ProfileController {
    private UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String getProfile(Model model) {
        UserDto userDto = new UserDto();

        model.addAttribute("userDto", userDto);

        return "profile/profile";
    }

    @PutMapping("profile/2fa")
    public String toggle2FA(RedirectAttributes redirAttr) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());

        boolean newStatus = !user.isUsing2FA();
        user.setUsing2FA(newStatus);
        userService.updateUser(user);

        Authentication newAuth = new UsernamePasswordAuthenticationToken(user, auth.getCredentials(),
                auth.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        if (newStatus) {
            String qrCode = userService.generateQRUrl(user);
            redirAttr.addFlashAttribute("qrCode", qrCode);
        }

        return "redirect:/profile";
    }

}
