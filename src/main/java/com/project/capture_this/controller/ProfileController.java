package com.project.capture_this.controller;

import com.project.capture_this.model.dto.AddProfilePictureDTO;
import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.model.entity.Role;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.UserRole;
import com.project.capture_this.service.*;
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
import java.util.Objects;

@Controller
public class ProfileController {

    private final UserService userService;
    private final PostService postService;
    private final FollowService followService;
    private final ProfileService profileService;
    private final RoleService roleService;

    public ProfileController(UserService userService, PostService postService, FollowService followService, ProfileService profileService, RoleService roleService) {
        this.userService = userService;
        this.postService = postService;
        this.followService = followService;
        this.profileService = profileService;
        this.roleService = roleService;
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        User loggedUser = userService.getLoggedUser();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedBirthDate = loggedUser.getBirthDate() != null
                ? loggedUser.getBirthDate().format(formatter)
                : "";

        DisplayUserDTO userDTO = UserService.mapToDisplayUserDTO(loggedUser);
        model.addAttribute("profileData", userDTO);
        model.addAttribute("posts", postService.findPublishedPosts());
        model.addAttribute("profilePictureData", new AddProfilePictureDTO());
        model.addAttribute("formattedBirthDate", formattedBirthDate);
        model.addAttribute("isOwnProfile", true);
        model.addAttribute("isAdmin", loggedUser.isAdmin());

        return "profile";
    }

    @GetMapping("/profile/{userId}")
    public String viewOtherUserProfile(@PathVariable Long userId, Model model) {
        if (Objects.equals(userId, userService.getLoggedUser().getId())) {
            return "redirect:/profile";
        }

        User user = userService.findById(userId);
        boolean isFollowing = followService.isFollowing(userService.getLoggedUser().getId(), userId);

        DisplayUserDTO userDTO = UserService.mapToDisplayUserDTO(user);
        model.addAttribute("profileData", userDTO);
        model.addAttribute("posts", postService.findPostsByUser(user));
        model.addAttribute("isOwnProfile", false);
        model.addAttribute("isFollowing", isFollowing);

        if (userService.getLoggedUser().isAdmin()) {
            model.addAttribute("profilePictureData", new AddProfilePictureDTO());
            model.addAttribute("isAdmin", true);
        } else {
            model.addAttribute("isAdmin", false);
        }
        model.addAttribute("userIsAdmin", user.isAdmin());
        return "profile";
    }

    @PostMapping("/profile/add-profile-picture")
    public String addProfilePicture(@Valid AddProfilePictureDTO profilePictureDTO,
                                    BindingResult bindingResult,
                                    @RequestParam(value = "userId", required = false) Long userId,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("profilePictureData", profilePictureDTO);
            redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "profilePictureData", bindingResult);
            return "redirect:/profile";
        }

        User loggedUser = userService.getLoggedUser();
        User targetUser = (userId != null && loggedUser.isAdmin()) ? userService.findById(userId) : loggedUser;

        profileService.saveProfilePicture(targetUser, profilePictureDTO.getProfilePicture());

        redirectAttributes.addFlashAttribute("message", "Profile picture updated successfully");
        return "redirect:/profile/" + (userId != null ? userId : "");
    }

    @PostMapping("/profile/change-bio")
    public String changeBio(@RequestParam("bio") String bio,
                            @RequestParam(value = "userId", required = false) Long userId,
                            RedirectAttributes redirectAttributes) {
        User loggedUser = userService.getLoggedUser();

        if (userId != null && loggedUser.isAdmin()) {
            profileService.updateBio(userId, bio);
            redirectAttributes.addFlashAttribute("message", "User's bio updated successfully");
            return "redirect:/profile/" + userId;
        }

        profileService.updateBio(loggedUser.getId(), bio);
        redirectAttributes.addFlashAttribute("message", "Bio updated successfully");
        return "redirect:/profile";
    }

    @PostMapping("/profile/change-first-name")
    public String changeFirstName(@RequestParam("firstName") String firstName,
                                  @RequestParam(value = "userId", required = false) Long userId,
                                  RedirectAttributes redirectAttributes) {
        User loggedUser = userService.getLoggedUser();

        if (userId != null && loggedUser.isAdmin()) {
            profileService.updateFirstName(userId, firstName);
            redirectAttributes.addFlashAttribute("message", "User's first name updated successfully");
            return "redirect:/profile/" + userId;
        }

        profileService.updateFirstName(loggedUser.getId(), firstName);
        redirectAttributes.addFlashAttribute("message", "First name updated successfully");
        return "redirect:/profile";
    }

    @PostMapping("/profile/change-last-name")
    public String changeLastName(@RequestParam("lastName") String lastName,
                                 @RequestParam(value = "userId", required = false) Long userId,
                                 RedirectAttributes redirectAttributes) {
        User loggedUser = userService.getLoggedUser();

        if (userId != null && loggedUser.isAdmin()) {
            profileService.updateLastName(userId, lastName);
            redirectAttributes.addFlashAttribute("message", "User's last name updated successfully");
            return "redirect:/profile/" + userId;
        }

        profileService.updateLastName(loggedUser.getId(), lastName);
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

    @PostMapping("/profile/make-admin/{userId}")
    public String toggleAdmin(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        User adminUser = userService.getLoggedUser();

        if (!adminUser.isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized action.");
            return "redirect:/profile";
        }

        User targetUser = userService.findById(userId);

        Role adminRole = roleService.findByName(UserRole.ADMIN);
        Role userRole = roleService.findByName(UserRole.USER);

        if (targetUser.isAdmin()) {
            targetUser.getRoles().clear();
            targetUser.getRoles().add(userRole);
            redirectAttributes.addFlashAttribute("success", targetUser.getUsername() + " is now a regular user.");
        } else {
            targetUser.getRoles().clear();
            targetUser.getRoles().add(adminRole);
            redirectAttributes.addFlashAttribute("success", targetUser.getUsername() + " is now an admin.");
        }

        userService.save(targetUser);
        return "redirect:/profile/" + userId;
    }
}