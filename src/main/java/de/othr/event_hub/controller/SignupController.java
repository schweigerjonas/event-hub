package de.othr.event_hub.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.othr.event_hub.dto.UserDto;
import de.othr.event_hub.model.Authority;
import de.othr.event_hub.model.User;
import de.othr.event_hub.service.UserService;
import jakarta.validation.Valid;

@Controller
public class SignupController {
    private UserService userService;

    public SignupController(UserService userService) {
        super();
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String getSignUp(Model model) {
        UserDto userDto = new UserDto();

        // default account role
        userDto.setRole("BENUTZER");

        model.addAttribute("userDto", userDto);

        return "signup";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute @Valid UserDto userDto,
            BindingResult result,
            RedirectAttributes attr,
            Model model) {
        if (result.hasErrors()) {
            System.out.println("Errors: " + result.getAllErrors().toString());
            return "signup";
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());

        Authority authority = new Authority();
        authority.setDescription(userDto.getRole());
        user.setAuthorities(List.of(authority));

        userService.saveUser(user);

        // TODO: maybe redirect directly to homepage -> would need manual
        // authentication/authorization
        // on new user
        return "redirect:/login";
    }

}
