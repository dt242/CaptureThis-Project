package com.project.capture_this.controller;

import com.project.capture_this.model.dto.AddProfilePictureDTO;
import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.model.dto.UserLoginDTO;
import com.project.capture_this.model.dto.UserRegisterDTO;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.service.PostService;
import com.project.capture_this.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.project.capture_this.service.UserService.mapToDisplayUserDTO;

@Controller
public class UserController {
    private final UserService userService;
    private final PostService postService;

    public UserController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/register")
    public String viewRegister(Model model) {
        if (!model.containsAttribute("registerData")) {
            model.addAttribute("registerData", new UserRegisterDTO());
        }
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(
            @Valid UserRegisterDTO data,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("registerData", data);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerData", bindingResult);
            return "redirect:/register";
        }

        boolean success = userService.register(data);

        if (!success) {
            bindingResult.reject("error.user", "An account already exists for this username or email.");
            redirectAttributes.addFlashAttribute("registerData", data);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerData", bindingResult);
            return "redirect:/register";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String viewLogin(Model model) {
        if (!model.containsAttribute("loginData")) {
            model.addAttribute("loginData", new UserLoginDTO());
        }
        return "login";
    }

    @GetMapping("/login-error")
    public String viewLoginError(Model model) {
        model.addAttribute("loginError", true);
        if (!model.containsAttribute("loginData")) {
            model.addAttribute("loginData", new UserLoginDTO());
        }
        return "login";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        User loggedUser = userService.getLoggedUser();
        if (loggedUser == null) {
            return "redirect:/login";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedBirthDate = loggedUser.getBirthDate() != null
                ? loggedUser.getBirthDate().format(formatter)
                : "";

        DisplayUserDTO userDTO = UserService.mapToDisplayUserDTO(loggedUser);
        model.addAttribute("profileData", userDTO);
        model.addAttribute("posts", postService.findPublishedPosts());
//        model.addAttribute("draftPosts", postService.findDraftPosts());
        model.addAttribute("profilePictureData", new AddProfilePictureDTO());
        model.addAttribute("formattedBirthDate", formattedBirthDate);
        model.addAttribute("isOwnProfile", true);

        return "profile";
    }

    @GetMapping("/profile/{userId}")
    public String viewOtherUserProfile(@PathVariable Long userId, Model model) {
        User user = userService.findById(userId);
        if (user == null) {
            return "redirect:/login";
        }

        boolean isFollowing = userService.isFollowing(userService.getLoggedUser().getId(), userId);

        DisplayUserDTO userDTO = UserService.mapToDisplayUserDTO(user);
        model.addAttribute("profileData", userDTO);
        model.addAttribute("posts", postService.findPostsByUser(user));
        model.addAttribute("isOwnProfile", false);
        model.addAttribute("isFollowing", isFollowing);

        return "profile"; // Use the same template
    }


    @PostMapping("/profile/add-profile-picture")
    public String addProfilePicture(@Valid AddProfilePictureDTO profilePictureDTO,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("profilePictureData", profilePictureDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.profilePictureData", bindingResult);
            return "redirect:/profile";
        }

        User loggedUser = userService.getLoggedUser();
        if (loggedUser != null) {
            userService.saveProfilePicture(loggedUser, profilePictureDTO.getProfilePicture());
        }

        return "redirect:/profile";
    }

    @PostMapping("/profile/change-bio")
    public String changeBio(@RequestParam("bio") String bio, RedirectAttributes redirectAttributes) {
        User loggedUser = userService.getLoggedUser();
        userService.updateBio(loggedUser.getId(), bio);
        redirectAttributes.addFlashAttribute("message", "Bio updated successfully");
        return "redirect:/profile";
    }

    @PostMapping("/profile/change-first-name")
    public String changeFirstName(@RequestParam("firstName") String firstName, RedirectAttributes redirectAttributes) {
        User loggedUser = userService.getLoggedUser();
        userService.updateFirstName(loggedUser.getId(), firstName);
        redirectAttributes.addFlashAttribute("message", "First name updated successfully");
        return "redirect:/profile";
    }

    @PostMapping("/profile/change-last-name")
    public String changeLastName(@RequestParam("lastName") String lastName, RedirectAttributes redirectAttributes) {
        User loggedUser = userService.getLoggedUser();
        userService.updateLastName(loggedUser.getId(), lastName);
        redirectAttributes.addFlashAttribute("message", "Last name updated successfully");
        return "redirect:/profile";
    }

    @GetMapping("/profile-picture/{userId}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long userId) {
        User user = userService.findById(userId);
        byte[] image = user.getProfilePicture();
        if (image != null) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public String searchUsers(@RequestParam(value = "query", required = false) String query, Model model) {
        List<DisplayUserDTO> results = new ArrayList<>();
        if (query != null && !query.isEmpty()) {
            results = userService.searchUsers(query);
        }

        model.addAttribute("query", query);
        model.addAttribute("results", results);
        return "search";
    }

    @PostMapping("/profile/follow/{userId}")
    public ResponseEntity<Map<String, Object>> followUser(@PathVariable("userId") Long userId) {
        Long currentUserId = userService.getLoggedUser().getId();
        userService.followUser(currentUserId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/profile/unfollow/{userId}")
    public ResponseEntity<Map<String, Object>> unfollowUser(@PathVariable("userId") Long userId) {
        Long currentUserId = userService.getLoggedUser().getId();
        userService.unfollowUser(currentUserId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/followers/{userId}")
    public String viewFollowers(@PathVariable Long userId, Model model) {
        List<User> followers = userService.getFollowers(userId);
        List<DisplayUserDTO> followerDTOs = followers.stream()
                .map(user -> new DisplayUserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getProfilePicture()))
                .collect(Collectors.toList());

        model.addAttribute("followers", followerDTOs);
        return "followers"; // This should correspond to followers.html
    }
}
