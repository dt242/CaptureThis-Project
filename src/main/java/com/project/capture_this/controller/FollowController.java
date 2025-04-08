package com.project.capture_this.controller;

import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.service.FollowService;
import com.project.capture_this.service.NotificationService;
import com.project.capture_this.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class FollowController {

    private final UserService userService;
    private final FollowService followService;
    private final NotificationService notificationService;

    public FollowController(UserService userService, FollowService followService, NotificationService notificationService) {
        this.userService = userService;
        this.followService = followService;
        this.notificationService = notificationService;
    }

    @PostMapping("/profile/follow/{userId}")
    public ResponseEntity<Map<String, Object>> followUser(@PathVariable("userId") Long userId) {
        User follower = userService.getLoggedUser();

        if (!follower.getId().equals(userId)) {
            followService.followUser(follower.getId(), userId);

            User followedUser = userService.findById(userId);
            notificationService.notifyFollow(follower, followedUser);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/profile/unfollow/{userId}")
    public ResponseEntity<Map<String, Object>> unfollowUser(@PathVariable("userId") Long userId) {
        Long currentUserId = userService.getLoggedUser().getId();
        followService.unfollowUser(currentUserId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/followers/{userId}")
    public String viewFollowers(@PathVariable Long userId, Model model) {
        List<User> followers = followService.getFollowers(userId);
        List<DisplayUserDTO> followerDTOs = followers.stream()
                .map(user -> new DisplayUserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getProfilePicture()))
                .collect(Collectors.toList());

        model.addAttribute("followers", followerDTOs);
        return "followers";
    }

    @GetMapping("/following/{userId}")
    public String viewFollowing(@PathVariable Long userId, Model model) {
        List<User> following = followService.getFollowing(userId);
        List<DisplayUserDTO> followingDTOs = following.stream()
                .map(user -> new DisplayUserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getProfilePicture()))
                .collect(Collectors.toList());

        model.addAttribute("following", followingDTOs);
        return "following";
    }
}
