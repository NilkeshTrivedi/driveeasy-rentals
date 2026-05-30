package com.driveeasy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    // Shows the login page
    // Spring Security handles the actual POST /login automatically
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage",
                    "Invalid username or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage",
                    "You have been logged out successfully.");
        }
        return "login";
    }

    // Access denied page
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}