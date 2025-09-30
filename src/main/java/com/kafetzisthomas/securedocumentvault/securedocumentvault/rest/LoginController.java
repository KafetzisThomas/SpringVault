package com.kafetzisthomas.securedocumentvault.securedocumentvault.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "users/login";
    }

    @GetMapping("/showAccessDenied")
    public String showAccessDenied() {
        return "users/access-denied";
    }

}
