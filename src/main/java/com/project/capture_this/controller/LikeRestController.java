package com.project.capture_this.controller;

import com.project.capture_this.model.entity.User;
import com.project.capture_this.service.LikeService;
import com.project.capture_this.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class LikeRestController {

    private final LikeService likeService;
    private final UserService userService;

    public LikeRestController(LikeService likeService, UserService userService) {
        this.likeService = likeService;
        this.userService = userService;
    }

    @PostMapping("/post/like/{postId}")
    public Map<String, Object> likePost(@PathVariable Long postId) {
        Map<String, Object> response = new HashMap<>();
        try {
            likeService.likePost(postId, userService.getLoggedUser().getId());
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;  // No need for ResponseEntity if you want the default status code (200 OK)
    }

    @PostMapping("/post/unlike/{postId}")
    public Map<String, Object> unlikePost(@PathVariable Long postId) {
        Map<String, Object> response = new HashMap<>();
        try {
            likeService.unlikePost(postId, userService.getLoggedUser().getId());
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @GetMapping("/post/likes/{postId}")
    public Map<String, Object> getLikes(@PathVariable Long postId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<User> usersWhoLiked = likeService.getUsersWhoLikedPost(postId);
            response.put("success", true);
            response.put("likes", usersWhoLiked);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
}

