package com.project.capture_this.service;

import com.project.capture_this.util.SecurityUtil;
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

    public List<DisplayUserDTO> searchUsers(String query) {
        List<User> users = userRepository.findByFirstNameContainingOrLastNameContaining(query, query);
        return users.stream().map(UserService::mapToDisplayUserDTO).collect(Collectors.toList());
    }

    public byte[] getDefaultProfilePicture() {
        try {
            Path path = Paths.get("src/main/resources/static/img/default-profile-picture.jpg");
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load default profile picture", e);
        }
    }

    public List<byte[]> getPostPictures() {
        try {
            List<byte[]> postPictures = new ArrayList<>();

            // List of file names
            List<String> fileNames = List.of(
                    "rand1.avif", "rand2.avif", "rand3.avif", "rand4.avif", "rand5.avif",
                    "rand6.avif", "rand7.avif", "rand8.avif", "rand9.avif", "rand10.avif",
                    "rand11.avif", "rand12.avif", "rand13.avif", "rand14.avif", "rand15.avif",
                    "rand16.avif", "rand17.avif", "rand18.avif", "rand19.avif", "rand20.avif",
                    "rand21.avif", "rand22.avif", "rand23.avif", "rand24.avif", "rand25.avif",
                    "rand26.avif", "rand27.avif", "rand28.avif", "rand29.avif", "rand30.avif",
                    "rand31.avif", "rand32.avif", "rand33.avif", "rand34.avif", "rand35.avif"
            );

            // Loop through file names and read them into the list
            for (String fileName : fileNames) {
                Path filePath = Paths.get("src/main/resources/static/img/" + fileName);
                postPictures.add(Files.readAllBytes(filePath));
            }

            return postPictures;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load post pictures", e);
        }
    }

    public List<byte[]> getProfilePictures() {
        try {
            List<byte[]> profilePictures = new ArrayList<>();

            // List of file names
            List<String> fileNames = List.of(
                    "pfp1.avif", "pfp2.avif", "pfp3.avif", "pfp4.avif", "pfp5.avif",
                    "pfp6.avif", "pfp7.avif", "pfp8.avif", "pfp9.avif", "pfp10.avif",
                    "pfp11.avif", "pfp12.avif"
            );

            // Loop through file names and read them into the list
            for (String fileName : fileNames) {
                Path filePath = Paths.get("src/main/resources/static/img/" + fileName);
                profilePictures.add(Files.readAllBytes(filePath));
            }

            return profilePictures;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load post pictures", e);
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

    public void save(User user) {
        userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
