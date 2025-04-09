package com.project.capture_this.controller;

import com.project.capture_this.util.SecurityUtil;
import com.project.capture_this.model.dto.DisplayPostDTO;
import com.project.capture_this.service.PostService;
import com.project.capture_this.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private final UserService userService;
    private final PostService postService;

    public HomeController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contacts")
    public String contacts() {
        return "contacts";
    }

    @GetMapping("/home")
    public String home(Model model) {
        List<DisplayPostDTO> allFollowedPosts = postService.findFollowedPosts();
        if (SecurityUtil.getSessionUser() != null) {
            model.addAttribute("user", userService.getLoggedUser());
        }
        model.addAttribute("posts", allFollowedPosts);
        return "home";
    }
}