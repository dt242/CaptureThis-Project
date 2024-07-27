package com.project.capture_this.controller;

import com.project.capture_this.model.dto.UserLoginDTO;
import com.project.capture_this.model.dto.UserRegisterDTO;
import com.project.capture_this.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String viewRegister(Model model) {
        model.addAttribute("registerData", new UserRegisterDTO());
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(
            @Valid UserRegisterDTO data,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
//        User existingUserEmail = userService.findByEmail(data.getEmail());
//        if(existingUserEmail != null && existingUserEmail.getEmail() != null && !existingUserEmail.getEmail().isEmpty()) {
//            return "redirect:/register?fail";
//        }
//        User existingUserUsername = userService.findByUsername(data.getUsername());
//        if(existingUserUsername != null && existingUserUsername.getUsername() != null && !existingUserUsername.getUsername().isEmpty()) {
//            return "redirect:/register?fail";
//        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("registerData", data);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerData", bindingResult);
            return "redirect:/register";
        }

        userService.register(data);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String viewLogin(Model model) {
        model.addAttribute("loginData", new UserLoginDTO());
        return "login";
    }

    @GetMapping("/login-error")
    public String viewLoginError(Model model) {
        model.addAttribute("showErrorMessage", true);
        model.addAttribute("loginData", new UserLoginDTO());
        return "login";
    }

//    @GetMapping("/profile")
//    public String profile(Model model) {
//        model.addAttribute("profileData", userService.getProfileData());
//        return "profile";
//    }
}
