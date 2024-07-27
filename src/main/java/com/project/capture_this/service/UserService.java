package com.project.capture_this.service;

import com.project.capture_this.config.SecurityUtil;
import com.project.capture_this.config.UserSession;
import com.project.capture_this.model.dto.UserLoginDTO;
import com.project.capture_this.model.dto.UserRegisterDTO;
import com.project.capture_this.model.entity.Role;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.UserRoles;
import com.project.capture_this.repository.RoleRepository;
import com.project.capture_this.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSession userSession;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserSession userSession, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSession = userSession;
        this.roleRepository = roleRepository;
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

        this.userRepository.save(user);

        return true;
    }

    public boolean login(UserLoginDTO data) {
        Optional<User> byUsername = userRepository.findByUsername(data.getUsername());

        if (byUsername.isEmpty()) {
            return false;
        }

        boolean passMatch = passwordEncoder
                .matches(data.getPassword(), byUsername.get().getPassword());

        if (!passMatch) {
            return false;
        }

        userSession.login(byUsername.get().getId(), data.getUsername());

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
}
