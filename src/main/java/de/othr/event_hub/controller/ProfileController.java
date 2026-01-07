package de.othr.event_hub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import de.othr.event_hub.dto.UserDto;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String getProfile(Model model) {
        UserDto userDto = new UserDto();

        model.addAttribute("userDto", userDto);

        return "profile/profile";
    }

}
