package com.project.capture_this.service;

import com.project.capture_this.config.SecurityUtil;
import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.model.dto.UserRegisterDTO;
import com.project.capture_this.model.entity.Role;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.UserRoles;
import com.project.capture_this.repository.RoleRepository;
import com.project.capture_this.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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

    public User getLoggedUser() {
        return userRepository.findByUsername(SecurityUtil.getSessionUser()).get();
    }

    public User findById(Long userId) {
        return userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
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

    public User findByUsername(String sessionUser) {
        return userRepository.findByUsername(sessionUser).get();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).get();
    }


    public List<DisplayUserDTO> searchUsers(String query) {
        List<User> users = userRepository.findByFirstNameContainingOrLastNameContaining(query, query);
        return users.stream().map(UserService::mapToDisplayUserDTO).collect(Collectors.toList());
    }

    private byte[] getDefaultProfilePicture() {
        try {
            Path path = Paths.get("src/main/resources/static/img/default-profile-picture.jpg");
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load default profile picture", e);
        }
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

    public List<User> findInactiveUsers(LocalDateTime lastActiveBefore) {
        return userRepository.findByUpdatedAtBeforeAndIsActiveTrue(lastActiveBefore);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public List<Long> getAllUserIds() {
        return userRepository.findAllUserIds();
    }
}
