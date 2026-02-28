package com.project.capture_this.service;

import com.project.capture_this.model.entity.User;
import com.project.capture_this.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ProfileService {

    private final UserRepository userRepository;

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveProfilePicture(User user, MultipartFile profilePicture) {
        try {
            user.setProfilePicture(profilePicture.getBytes());
            userRepository.save(user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process profile picture", e);
        }
    }

    @Transactional
    public void updateBio(Long userId, String bio) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setBio(bio);
    }

    @Transactional
    public void updateFirstName(Long userId, String firstName) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstName(firstName);
    }

    @Transactional
    public void updateLastName(Long userId, String lastName) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setLastName(lastName);
    }
}