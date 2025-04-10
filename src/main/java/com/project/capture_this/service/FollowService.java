package com.project.capture_this.service;

import com.project.capture_this.model.entity.User;
import com.project.capture_this.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FollowService {

    private final UserRepository userRepository;

    public FollowService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void followUser(Long followerId, Long followeeId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + followerId));
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + followeeId));

        if (!follower.getFollowing().contains(followee)) {
            follower.getFollowing().add(followee);
            followee.getFollowers().add(follower);
            userRepository.save(follower);
            userRepository.save(followee);
        }
    }

    @Transactional
    public void unfollowUser(Long followerId, Long followeeId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + followerId));
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + followeeId));

        if (follower.getFollowing().contains(followee)) {
            follower.getFollowing().remove(followee);
            followee.getFollowers().remove(follower);
            userRepository.save(follower);
            userRepository.save(followee);
        }
    }

    @Transactional
    public List<User> getFollowers(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return new ArrayList<>(user.getFollowers());
    }

    @Transactional
    public List<User> getFollowing(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return new ArrayList<>(user.getFollowing());
    }

    @Transactional
    public boolean isFollowing(Long followerId, Long followeeId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + followerId));
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + followeeId));
        return follower.getFollowing().contains(followee);
    }
}
