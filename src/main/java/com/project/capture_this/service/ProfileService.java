package com.project.capture_this.service;

import com.project.capture_this.model.entity.User;
import com.project.capture_this.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final UserService userService;

    public ProfileService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public void saveProfilePicture(User user, MultipartFile profilePicture) {
        try {
            user.setProfilePicture(profilePicture.getBytes());
            userRepository.save(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateBio(Long userId, String bio) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setBio(bio);
        userRepository.save(user);
    }

    public void updateFirstName(Long userId, String firstName) {
        User user = userService.findById(userId);
        user.setFirstName(firstName);
        userRepository.save(user);
    }

    public void updateLastName(Long userId, String lastName) {
        User user = userService.findById(userId);
        user.setLastName(lastName);
        userRepository.save(user);
    }
}
