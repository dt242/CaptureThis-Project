package com.project.capture_this.controller;

import com.project.capture_this.model.dto.DisplayPostDTO;
import com.project.capture_this.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private final PostService postService;

    public HomeController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/home")
    public String home(Model model) {
        List<DisplayPostDTO> allFollowedPosts = postService.findFollowedPosts();
        model.addAttribute("posts", allFollowedPosts);
        return "home";
    }
}