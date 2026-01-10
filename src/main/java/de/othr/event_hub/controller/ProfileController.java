package de.othr.event_hub.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

import de.othr.event_hub.config.AccountUserDetails;
import de.othr.event_hub.dto.UpdatePasswordDto;
import de.othr.event_hub.dto.UpdateUserInfoDto;
import de.othr.event_hub.dto.UserDto;
import de.othr.event_hub.model.User;
import de.othr.event_hub.service.UserService;
import de.othr.event_hub.util.UserMapper;
import de.othr.event_hub.validator.PasswordUpdateValidator;
import de.othr.event_hub.validator.ProfileUpdateValidator;
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

    @InitBinder("userInfoDto")
    public void initProfileBinder(WebDataBinder binder) {
        binder.addValidators(new ProfileUpdateValidator(userService));
    }

    @InitBinder("updatePasswordDto")
    public void initPasswordBinder(WebDataBinder binder) {
        binder.addValidators(new PasswordUpdateValidator(userService, passwordEncoder));
    }

    @GetMapping("/profile")
    public String getProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        UserDto userDto = new UserDto();
        UpdateUserInfoDto userInfoDto = new UpdateUserInfoDto();
        UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto();

        userDto.setRole(user.getAuthorities().get(0).getDescription());
        userInfoDto.setUsername(user.getUsername());
        userInfoDto.setEmail(user.getEmail());

        model.addAttribute("userDto", userDto);
        model.addAttribute("userInfoDto", userInfoDto);
        model.addAttribute("updatePasswordDto", updatePasswordDto);

        return "profile/profile";
    }

    @PutMapping("profile")
    public String updateUser(@ModelAttribute @Valid UpdateUserInfoDto userInfoDto, BindingResult result, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());

        if (result.hasErrors()) {
            System.out.println("Errors: " + result.getAllErrors().toString());

            UserDto userDto = new UserDto();
            userDto.setRole(user.getAuthorities().get(0).getDescription());

            model.addAttribute("userDto", userDto);
            model.addAttribute("updatePasswordDto", new UpdatePasswordDto());

            return "profile/profile";
        }

        try {
            userService.updateUserInfo(auth.getName(), userInfoDto);

            refreshAuthentication(userInfoDto.getUsername());
        } catch (Exception e) {
            System.err.println(e);
        }

        return "redirect:/profile";
    }

    @PutMapping("profile/role")
    public String updateRole(@ModelAttribute UserDto userDto, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());

        String newRole = userDto.getRole();

        try {
            userService.updateUserAuthority(user.getUsername(), newRole);

            refreshAuthentication(user.getUsername());

        } catch (Exception e) {
            System.err.println(e);
            return "profile/profile";
        }

        UpdateUserInfoDto userInfoDto = new UpdateUserInfoDto();
        userInfoDto.setUsername(user.getUsername());
        userInfoDto.setEmail(user.getEmail());

        model.addAttribute("userInfoDto", userInfoDto);
        model.addAttribute("updatePasswordDto", new UpdatePasswordDto());

        return "redirect:/profile";
    }

    @PutMapping("profile/password")
    public String updatePassword(@ModelAttribute @Valid UpdatePasswordDto updatePasswordDto, BindingResult result,
            Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());

        if (result.hasErrors()) {
            System.out.println("Errors: " + result.getAllErrors().toString());

            UpdateUserInfoDto userInfoDto = new UpdateUserInfoDto();
            userInfoDto.setUsername(user.getUsername());
            userInfoDto.setEmail(user.getEmail());

            model.addAttribute("userDto", new UserMapper().toDto(user));
            model.addAttribute("userInfoDto", userInfoDto);

            return "profile/profile";
        }

        String redirectRoute = "redirect:/profile";

        try {
            userService.updatePassword(user.getUsername(), updatePasswordDto.getNewPassword());

            redirectRoute = "redirect:/profile?success";
        } catch (Exception e) {
            System.err.println(e);
        }

        UpdateUserInfoDto userInfoDto = new UpdateUserInfoDto();
        userInfoDto.setUsername(user.getUsername());
        userInfoDto.setEmail(user.getEmail());

        model.addAttribute("userDto", new UserMapper().toDto(user));
        model.addAttribute("userInfoDto", userInfoDto);

        return redirectRoute;
    }

    @PutMapping("profile/2fa")
    public String toggle2FA(RedirectAttributes attr, Model model) {
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

        UserDto userDto = new UserDto();
        UpdateUserInfoDto userInfoDto = new UpdateUserInfoDto();
        UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto();

        userDto.setRole(user.getAuthorities().get(0).getDescription());
        userInfoDto.setUsername(user.getUsername());
        userInfoDto.setEmail(user.getEmail());

        model.addAttribute("userDto", userDto);
        model.addAttribute("userInfoDto", userInfoDto);
        model.addAttribute("updatePasswordDto", updatePasswordDto);

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

    public void refreshAuthentication(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(username);

        List<SimpleGrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getDescription())).collect(Collectors.toList());

        AccountUserDetails userDetails = new AccountUserDetails(user);

        Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, auth.getCredentials(),
                grantedAuthorities);

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
