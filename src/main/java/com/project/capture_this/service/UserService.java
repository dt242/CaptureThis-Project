package com.project.capture_this.service;

import com.project.capture_this.util.SecurityUtil;
import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.model.dto.UserRegisterDTO;
import com.project.capture_this.model.entity.Role;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.UserRole;
import com.project.capture_this.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Transactional(readOnly = true)
    public User getLoggedUser() {
        return userRepository.findByUsername(SecurityUtil.getSessionUser())
                .orElseThrow(() -> new EntityNotFoundException("Logged user not found in database"));
    }

    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }

    @Transactional
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
        Role role = roleService.findByName(UserRole.USER);

        user.setRoles(Set.of(role));
        user.setProfilePicture(getDefaultProfilePicture());

        this.userRepository.save(user);

        return true;
    }

    @Transactional(readOnly = true)
    public List<DisplayUserDTO> searchUsers(String query) {
        List<User> users = userRepository.findByFirstNameContainingOrLastNameContaining(query, query);
        return users.stream().map(UserService::mapToDisplayUserDTO).collect(Collectors.toList());
    }

    private byte[] readResourceFile(String filePath) {
        try (InputStream is = new ClassPathResource(filePath).getInputStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load resource file: " + filePath, e);
        }
    }

    public byte[] getDefaultProfilePicture() {
        return readResourceFile("static/img/default-profile-picture.jpg");
    }

    public List<byte[]> getPostPictures() {
        List<byte[]> postPictures = new ArrayList<>();
        List<String> fileNames = List.of(
                "rand1.avif", "rand2.avif", "rand3.avif", "rand4.avif", "rand5.avif",
                "rand6.avif", "rand7.avif", "rand8.avif", "rand9.avif", "rand10.avif",
                "rand11.avif", "rand12.avif", "rand13.avif", "rand14.avif", "rand15.avif",
                "rand16.avif", "rand17.avif", "rand18.avif", "rand19.avif", "rand20.avif",
                "rand21.avif", "rand22.avif", "rand23.avif", "rand24.avif", "rand25.avif",
                "rand26.avif", "rand27.avif", "rand28.avif", "rand29.avif", "rand30.avif",
                "rand31.avif", "rand32.avif", "rand33.avif", "rand34.avif", "rand35.avif"
        );

        for (String fileName : fileNames) {
            postPictures.add(readResourceFile("static/img/" + fileName));
        }
        return postPictures;
    }

    public List<byte[]> getProfilePictures() {
        List<byte[]> profilePictures = new ArrayList<>();
        List<String> fileNames = List.of(
                "pfp1.avif", "pfp2.avif", "pfp3.avif", "pfp4.avif", "pfp5.avif",
                "pfp6.avif", "pfp7.avif", "pfp8.avif", "pfp9.avif", "pfp10.avif",
                "pfp11.avif", "pfp12.avif"
        );

        for (String fileName : fileNames) {
            profilePictures.add(readResourceFile("static/img/" + fileName));
        }
        return profilePictures;
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
                .bio(user.getBio())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}