package com.kafetzisthomas.securedocumentvault.securedocumentvault.rest;

import com.kafetzisthomas.securedocumentvault.securedocumentvault.dto.RegistrationForm;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("form", new RegistrationForm());
        return "users/register";
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute("form") RegistrationForm form,
                             Model model, RedirectAttributes redirectAttributes) {

        // basic checks without heavy validation
        if (form.getUsername() == null || form.getUsername().isBlank()
                || form.getPassword() == null || form.getPassword().isBlank()) {
            model.addAttribute("error", "Username and password are required.");
            return "users/register";
        }

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match.");
            return "users/register";
        }

        if (userDetailsManager.userExists(form.getUsername())) {
            model.addAttribute("error", "User already exists.");
            return "users/register";
        }

        UserDetails user = User.withUsername(form.getUsername())
                .passwordEncoder(passwordEncoder::encode)
                .password(form.getPassword())
                .roles("USER")
                .build();

        userDetailsManager.createUser(user);

        redirectAttributes.addFlashAttribute("registered", true);
        return "redirect:/login";
    }

}
