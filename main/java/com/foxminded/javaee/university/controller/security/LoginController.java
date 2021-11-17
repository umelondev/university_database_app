package com.foxminded.javaee.university.controller.security;

import com.foxminded.javaee.university.model.security.User;
import com.foxminded.javaee.university.service.security.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/login")
    public String showLoginForm() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }

        return "redirect:/";
    }

    @PostMapping(path = "/registration")
    public String postRegistrationPage(User user, RedirectAttributes redirAttrs) {
        if (userService.isUserExist(user))
            redirAttrs.addFlashAttribute("regError", String.format("User \"%s\" exists!", user.getUsername()));
        else {
            userService.save(user);
            redirAttrs.addFlashAttribute("success", "Successfully registered!");
        }
        return "redirect:/login";
    }
}
