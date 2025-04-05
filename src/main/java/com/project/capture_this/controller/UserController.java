package com.project.capture_this.controller;

import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.model.dto.UserLoginDTO;
import com.project.capture_this.model.dto.UserRegisterDTO;
import com.project.capture_this.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String viewRegister(Model model) {
        if (!model.containsAttribute("registerData")) {
            model.addAttribute("registerData", new UserRegisterDTO());
        }
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(
            @Valid UserRegisterDTO data,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("registerData", data);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerData", bindingResult);
            return "redirect:/register";
        }

        boolean success = userService.register(data);

        if (!success) {
            bindingResult.reject("error.user", "An account already exists for this username or email.");
            redirectAttributes.addFlashAttribute("registerData", data);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerData", bindingResult);
            return "redirect:/register";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String viewLogin(Model model) {
        if (!model.containsAttribute("loginData")) {
            model.addAttribute("loginData", new UserLoginDTO());
        }
        return "login";
    }

    @GetMapping("/login-error")
    public String viewLoginError(Model model) {
        model.addAttribute("loginError", true);
        if (!model.containsAttribute("loginData")) {
            model.addAttribute("loginData", new UserLoginDTO());
        }
        return "login";
    }

    @GetMapping("/search")
    public String searchUsers(@RequestParam(value = "query", required = false) String query, Model model) {
        List<DisplayUserDTO> results = new ArrayList<>();
        if (query != null && !query.isEmpty()) {
            results = userService.searchUsers(query);
        }

        model.addAttribute("query", query);
        model.addAttribute("results", results);
        return "search";
    }
}
