package com.project.capture_this.controller;

import com.project.capture_this.service.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/profile/follow/{userId}")
    public ResponseEntity<Map<String, Object>> followUser(@PathVariable("userId") Long userId) {
        followService.followUser(userId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/profile/unfollow/{userId}")
    public ResponseEntity<Map<String, Object>> unfollowUser(@PathVariable("userId") Long userId) {
        followService.unfollowUser(userId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/followers/{userId}")
    public String viewFollowers(@PathVariable Long userId, Model model) {
        model.addAttribute("followers", followService.getFollowersDTOs(userId));
        return "followers";
    }

    @GetMapping("/following/{userId}")
    public String viewFollowing(@PathVariable Long userId, Model model) {
        model.addAttribute("following", followService.getFollowingDTOs(userId));
        return "following";
    }
}