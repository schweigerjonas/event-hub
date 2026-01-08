package de.othr.event_hub.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.othr.event_hub.dto.UpdatePasswordDto;
import de.othr.event_hub.dto.UserDto;
import de.othr.event_hub.model.User;
import de.othr.event_hub.service.UserService;
import de.othr.event_hub.util.UserMapper;
import de.othr.event_hub.validator.PasswordUpdateValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ProfileController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    public ProfileController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @InitBinder("updatePasswordDto")
    public void InitBinder(WebDataBinder binder) {
        binder.addValidators(new PasswordUpdateValidator(userService, passwordEncoder));
    }

    @GetMapping("/profile")
    public String getProfile(Model model) {
        UserDto userDto = new UserDto();
        UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto();

        model.addAttribute("userDto", userDto);
        model.addAttribute("updatePasswordDto", updatePasswordDto);

        return "profile/profile";
    }

    @PutMapping("profile/password")
    public String updatePassword(@ModelAttribute @Valid UpdatePasswordDto updatePasswordDto, BindingResult result,
            RedirectAttributes attr, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());

        if (result.hasErrors()) {
            System.out.println("Errors: " + result.getAllErrors().toString());

            model.addAttribute("userDto", new UserMapper().toDto(user));

            return "profile/profile";
        }

        try {
            userService.updatePassword(user.getUsername(), updatePasswordDto.getNewPassword());

            return "redirect:/profile?success";
        } catch (Exception e) {
            return "redirect:/profile";
        }

    }

    @PutMapping("profile/2fa")
    public String toggle2FA(RedirectAttributes attr) {
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
            attr.addFlashAttribute("qrCode", qrCode);
        }

        return "redirect:/profile";
    }

    @DeleteMapping("profile")
    public String deleteProfile(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        try {
            userService.softDeleteUserByUsername(auth.getName());
            SecurityContextHolder.clearContext();
            HttpSession session = request.getSession(false);

            if (session != null) {
                session.invalidate();
            }

            return "redirect:/login?deleted=true";
        } catch (Exception e) {
            System.err.println(e);
            return "redirect:/profile";
        }
    }

}
