package com.project.capture_this.service;

import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FollowService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    public FollowService(UserRepository userRepository, UserService userService, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Transactional
    public void followUser(Long followeeId) {
        User follower = userService.getLoggedUser();

        if (follower.getId().equals(followeeId)) {
            throw new IllegalArgumentException("User cannot follow themselves.");
        }

        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new EntityNotFoundException("Followee not found with ID: " + followeeId));

        if (!follower.getFollowing().contains(followee)) {
            follower.getFollowing().add(followee);
            followee.getFollowers().add(follower);
            notificationService.notifyFollow(follower, followee);
        }
    }

    @Transactional
    public void unfollowUser(Long followeeId) {
        User follower = userService.getLoggedUser();

        if (follower.getId().equals(followeeId)) {
            throw new IllegalArgumentException("User cannot unfollow themselves.");
        }

        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new EntityNotFoundException("Followee not found with ID: " + followeeId));

        if (follower.getFollowing().contains(followee)) {
            follower.getFollowing().remove(followee);
            followee.getFollowers().remove(follower);
        }
    }

    public List<DisplayUserDTO> getFollowersDTOs(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        return user.getFollowers().stream()
                .map(UserService::mapToDisplayUserDTO)
                .collect(Collectors.toList());
    }

    public List<DisplayUserDTO> getFollowingDTOs(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        return user.getFollowing().stream()
                .map(UserService::mapToDisplayUserDTO)
                .collect(Collectors.toList());
    }

    public boolean isFollowing(Long followerId, Long followeeId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Follower not found with ID: " + followerId));
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new EntityNotFoundException("Followee not found with ID: " + followeeId));
        return follower.getFollowing().contains(followee);
    }
}