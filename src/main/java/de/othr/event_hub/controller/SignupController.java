package de.othr.event_hub.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.othr.event_hub.model.Authority;
import de.othr.event_hub.model.User;
import jakarta.validation.Valid;

@Controller
public class SignupController {
    @GetMapping("/signup")
    public String getSignUp(Model model) {
        User user = new User();

        // create default authority for all accounts
        Authority defaultAuthority = new Authority();
        defaultAuthority.setDescription("BENUTZER");

        // set default authority
        List<Authority> userAuthorities = user.getAuthorities();
        userAuthorities.add(defaultAuthority);
        user.setAuthorities(userAuthorities);

        model.addAttribute("user", user);

        return "signup";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute @Valid User user, BindingResult result, RedirectAttributes attr,
            Model model) {
        if (result.hasErrors()) {
            // System.out.println("Errors: " + result.getAllErrors());
            return "signup";
        }

        System.out.println("Registered:");
        System.out.println("\tEmail: " + user.getEmail());
        System.out.println("\tUsername: " + user.getUsername());
        System.out.println("\tPassword: " + user.getPassword());
        System.out.println("\tAuthorities: " + user.getAuthorities());

        return "redirect:/";
    }

}
