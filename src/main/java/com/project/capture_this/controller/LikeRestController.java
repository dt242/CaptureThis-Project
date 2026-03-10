package com.project.capture_this.controller;

import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class LikeRestController {

    private final LikeService likeService;

    public LikeRestController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/post/like/{postId}")
    public ResponseEntity<Map<String, Object>> likePost(@PathVariable Long postId) {
        try {
            likeService.likePost(postId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/post/unlike/{postId}")
    public ResponseEntity<Map<String, Object>> unlikePost(@PathVariable Long postId) {
        try {
            likeService.unlikePost(postId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/post/likes/{postId}")
    public ResponseEntity<Map<String, Object>> getLikes(@PathVariable Long postId) {
        try {
            List<DisplayUserDTO> usersList = likeService.getUsersWhoLikedPostDTOs(postId);
            return ResponseEntity.ok(Map.of("success", true, "likes", usersList));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}