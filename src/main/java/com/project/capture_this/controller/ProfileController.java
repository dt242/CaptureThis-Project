package com.project.capture_this.controller;

import com.project.capture_this.model.dto.AddProfilePictureDTO;
import com.project.capture_this.service.ProfileService;
import com.project.capture_this.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
public class ProfileController {
    private final ProfileService profileService;
    private final UserService userService;

    public ProfileController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        model.addAllAttributes(profileService.getOwnProfileDetails());
        model.addAttribute("profilePictureData", new AddProfilePictureDTO());
        return "profile";
    }

    @GetMapping("/profile/{userId}")
    public String viewOtherUserProfile(@PathVariable Long userId, Model model) {
        if (Objects.equals(userId, userService.getLoggedUser().getId())) {
            return "redirect:/profile";
        }
        model.addAllAttributes(profileService.getOtherUserProfileDetails(userId));
        model.addAttribute("profilePictureData", new AddProfilePictureDTO());
        return "profile";
    }

    @PostMapping("/profile/add-profile-picture")
    public String addProfilePicture(@Valid AddProfilePictureDTO profilePictureData,
                                    BindingResult bindingResult,
                                    @RequestParam(value = "userId", required = false) Long userId,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("profilePictureData", profilePictureData);
            redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "profilePictureData", bindingResult);
            return "redirect:/profile";
        }
        profileService.updateProfilePicture(userId, profilePictureData.getProfilePicture());
        redirectAttributes.addFlashAttribute("message", "Profile picture updated successfully");
        return userId != null ? "redirect:/profile/" + userId : "redirect:/profile";
    }

    @PostMapping("/profile/change-bio")
    public String changeBio(@RequestParam("bio") String bio,
                            @RequestParam(value = "userId", required = false) Long userId,
                            RedirectAttributes redirectAttributes) {
        profileService.updateBio(userId, bio);
        redirectAttributes.addFlashAttribute("message", "Bio updated successfully");
        return userId != null ? "redirect:/profile/" + userId : "redirect:/profile";
    }

    @PostMapping("/profile/change-first-name")
    public String changeFirstName(@RequestParam("firstName") String firstName,
                                  @RequestParam(value = "userId", required = false) Long userId,
                                  RedirectAttributes redirectAttributes) {
        profileService.updateFirstName(userId, firstName);
        redirectAttributes.addFlashAttribute("message", "First name updated successfully");
        return userId != null ? "redirect:/profile/" + userId : "redirect:/profile";
    }

    @PostMapping("/profile/change-last-name")
    public String changeLastName(@RequestParam("lastName") String lastName,
                                 @RequestParam(value = "userId", required = false) Long userId,
                                 RedirectAttributes redirectAttributes) {
        profileService.updateLastName(userId, lastName);
        redirectAttributes.addFlashAttribute("message", "Last name updated successfully");
        return userId != null ? "redirect:/profile/" + userId : "redirect:/profile";
    }

    @GetMapping("/profile-picture/{userId}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long userId) {
        byte[] image = profileService.getProfilePictureBytes(userId);
        if (image != null) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/profile/make-admin/{userId}")
    public String toggleAdmin(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            String resultMessage = profileService.toggleAdmin(userId);
            redirectAttributes.addFlashAttribute("success", resultMessage);
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile/" + userId;
    }
}