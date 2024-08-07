package com.project.capture_this.service;

import com.project.capture_this.config.SecurityUtil;
import com.project.capture_this.model.dto.DisplayPostDTO;
import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.model.dto.UserRegisterDTO;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.Role;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.UserRoles;
import com.project.capture_this.repository.RoleRepository;
import com.project.capture_this.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    private byte[] getDefaultProfilePicture() {
        try {
            Path path = Paths.get("src/main/resources/static/img/default-profile-picture.jpg");
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load default profile picture", e);
        }
    }

    public boolean register(UserRegisterDTO data) {
        Optional<User> existingUser = userRepository
                .findByUsernameOrEmail(data.getUsername(), data.getEmail());

        if (existingUser.isPresent()) {
            return false;
        }

        User user = new User();

        user.setFirstName(data.getFirstName());
        user.setLastName(data.getLastName());
        user.setUsername(data.getUsername());
        user.setGender(data.getGender());
        user.setEmail(data.getEmail());
        user.setBirthDate(data.getBirthDate());
        user.setPassword(passwordEncoder.encode(data.getPassword()));
        Role role = roleRepository.findByName(UserRoles.USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRoles(Set.of(role));
        user.setProfilePicture(getDefaultProfilePicture());

        this.userRepository.save(user);

        return true;
    }

    public User getLoggedUser() {
        return userRepository.findByUsername(SecurityUtil.getSessionUser()).get();
    }

//    public List<User> searchUsers(String query) {
//        List<User> users = userRepository.searchUsers(query);
//        return users.stream().map(user -> mapToDisplayUserDto(user)).collect(Collectors.toList());
//    }

    public User findByUsername(String sessionUser) {
        return userRepository.findByUsername(sessionUser).get();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).get();
    }

    public void saveProfilePicture(User user, MultipartFile profilePicture) {
        try {
            user.setProfilePicture(profilePicture.getBytes());
            userRepository.save(user);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error appropriately
        }
    }

    public void updateBio(Long userId, String bio) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setBio(bio);
        userRepository.save(user);
    }

    public void updateFirstName(Long userId, String firstName) {
        User user = findById(userId);
        user.setFirstName(firstName);
        userRepository.save(user);
    }

    public void updateLastName(Long userId, String lastName) {
        User user = findById(userId);
        user.setLastName(lastName);
        userRepository.save(user);
    }

    public List<DisplayUserDTO> searchUsers(String query) {
        List<User> users = userRepository.findByFirstNameContainingOrLastNameContaining(query, query);
        return users.stream().map(UserService::mapToDisplayUserDTO).collect(Collectors.toList());
    }

    public static DisplayUserDTO mapToDisplayUserDTO(User user) {
        return DisplayUserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .profilePicture(user.getProfilePicture())
                .bio(user.getBio())
                .followers(user.getFollowers())
                .following(user.getFollowing())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
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

    public List<User> getFollowers(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return new ArrayList<>(user.getFollowers());
    }

    @Transactional
    public boolean isFollowing(Long followerId, Long followeeId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + followerId));
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + followeeId));
        return follower.getFollowing().contains(followee);
    }

//    @Transactional
//    public DisplayUserDTO viewOtherUserProfile(Long userId) {
//        User user = userRepository.findByIdWithDetails(userId);
//        return mapToDisplayUserDTO(user);
//    }
}
