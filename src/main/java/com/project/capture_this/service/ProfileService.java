package com.project.capture_this.service;

import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.model.entity.Role;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.UserRole;
import com.project.capture_this.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ProfileService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PostService postService;
    private final FollowService followService;
    private final RoleService roleService;

    public ProfileService(UserRepository userRepository, UserService userService, PostService postService, FollowService followService, RoleService roleService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.postService = postService;
        this.followService = followService;
        this.roleService = roleService;
    }

    public Map<String, Object> getOwnProfileDetails() {
        User loggedUser = userService.getLoggedUser();
        String formattedBirthDate = formatBirthDate(loggedUser);
        DisplayUserDTO userDTO = UserService.mapToDisplayUserDTO(loggedUser);

        return Map.of(
                "profileData", userDTO,
                "posts", postService.findPublishedPosts(),
                "formattedBirthDate", formattedBirthDate,
                "isOwnProfile", true,
                "isAdmin", loggedUser.isAdmin()
        );
    }

    public Map<String, Object> getOtherUserProfileDetails(Long userId) {
        User loggedUser = userService.getLoggedUser();
        User targetUser = userService.findById(userId);
        boolean isFollowing = followService.isFollowing(loggedUser.getId(), userId);
        DisplayUserDTO userDTO = UserService.mapToDisplayUserDTO(targetUser);

        return Map.of(
                "profileData", userDTO,
                "posts", postService.findPostsByUser(targetUser),
                "isOwnProfile", false,
                "isFollowing", isFollowing,
                "isAdmin", loggedUser.isAdmin(),
                "userIsAdmin", targetUser.isAdmin()
        );
    }

    public byte[] getProfilePictureBytes(Long userId) {
        User user = userService.findById(userId);
        return user.getProfilePicture();
    }

    private User resolveTargetUser(Long targetUserId) {
        User loggedUser = userService.getLoggedUser();
        if (targetUserId != null && loggedUser.isAdmin()) {
            return userRepository.findById(targetUserId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + targetUserId));
        }
        return loggedUser;
    }

    @Transactional
    public void updateProfilePicture(Long targetUserId, MultipartFile profilePicture) {
        if (profilePicture == null || profilePicture.isEmpty()) {
            throw new IllegalArgumentException("Profile picture file cannot be null or empty");
        }
        User targetUser = resolveTargetUser(targetUserId);
        try {
            targetUser.setProfilePicture(profilePicture.getBytes());
            userRepository.save(targetUser);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to process profile picture", e);
        }
    }

    @Transactional
    public void updateBio(Long targetUserId, String bio) {
        User targetUser = resolveTargetUser(targetUserId);
        targetUser.setBio(bio);
    }

    @Transactional
    public void updateFirstName(Long targetUserId, String firstName) {
        User targetUser = resolveTargetUser(targetUserId);
        targetUser.setFirstName(firstName);
    }

    @Transactional
    public void updateLastName(Long targetUserId, String lastName) {
        User targetUser = resolveTargetUser(targetUserId);
        targetUser.setLastName(lastName);
    }

    @Transactional
    public String toggleAdmin(Long targetUserId) {
        User adminUser = userService.getLoggedUser();
        if (!adminUser.isAdmin()) {
            throw new SecurityException("Unauthorized action.");
        }
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Role adminRole = roleService.findByName(UserRole.ADMIN);
        Role userRole = roleService.findByName(UserRole.USER);
        if (targetUser.isAdmin()) {
            targetUser.getRoles().clear();
            targetUser.getRoles().add(userRole);
            return targetUser.getUsername() + " is now a regular user.";
        } else {
            targetUser.getRoles().clear();
            targetUser.getRoles().add(adminRole);
            return targetUser.getUsername() + " is now an admin.";
        }
    }

    private String formatBirthDate(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return user.getBirthDate() != null ? user.getBirthDate().format(formatter) : "";
    }
}