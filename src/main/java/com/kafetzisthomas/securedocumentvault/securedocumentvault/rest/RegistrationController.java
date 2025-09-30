package com.kafetzisthomas.securedocumentvault.securedocumentvault.rest;

import com.kafetzisthomas.securedocumentvault.securedocumentvault.dao.UserRepository;
import com.kafetzisthomas.securedocumentvault.securedocumentvault.dto.RegistrationForm;
import com.kafetzisthomas.securedocumentvault.securedocumentvault.entity.AppUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
public class RegistrationController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
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

        if (userRepository.findByUsername(form.getUsername()).isPresent()) {
            model.addAttribute("error", "User already exists.");
            return "users/register";
        }

        AppUser user = new AppUser();
        user.setId(UUID.randomUUID());
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setEnabled(true);
        user.setRoles(List.of("ROLE_USER"));

        userRepository.save(user);

        redirectAttributes.addFlashAttribute("registered", true);
        return "redirect:/login";
    }

}
