package com.project.capture_this.controller;

import com.project.capture_this.model.dto.CreatePostDTO;
import com.project.capture_this.model.dto.DisplayPostDTO;
import com.project.capture_this.model.dto.EditPostDTO;
import com.project.capture_this.service.*;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/create-post")
    public String viewCreatePost(Model model) {
        if (!model.containsAttribute("createPostData")) {
            model.addAttribute("createPostData", new CreatePostDTO());
        }
        return "create-post";
    }

    @PostMapping("/create-post")
    public String doCreatePost(@Valid @ModelAttribute("createPostData") CreatePostDTO data,
                               BindingResult bindingResult,
                               @RequestParam("action") String action,
                               RedirectAttributes redirectAttributes) {

        if (data.getImageFile() == null || data.getImageFile().isEmpty()) {
            bindingResult.rejectValue("imageFile", "error.createPostData", "Photo is mandatory");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("createPostData", data);
            redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "createPostData", bindingResult);
            return "redirect:/create-post";
        }

        try {
            postService.savePost(data, action);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error uploading image. Please try again.");
            return "redirect:/create-post";
        }

        return "redirect:/profile";
    }

    @GetMapping("/images/{postId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long postId) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(postService.findImageByPostId(postId));
    }

    @GetMapping("/drafts/{userId}")
    public String viewDraftsByUser(@PathVariable Long userId, Model model) {
        List<DisplayPostDTO> drafts = postService.findDraftPostsByUser(userId);
        model.addAttribute("drafts", drafts);
        return "drafts";
    }

    @GetMapping("/edit-post/{id}")
    public String viewEditPost(@PathVariable Long id, Model model) {
        if (!model.containsAttribute("editPostData")) {
            EditPostDTO dto = postService.getPostForEditing(id);
            model.addAttribute("editPostData", dto);
        }
        model.addAttribute("isAlreadyPosted", postService.isPostStatusPublished(id));
        return "edit-post";
    }

    @PostMapping("/edit-post")
    public String doEditPost(@Valid @ModelAttribute("editPostData") EditPostDTO data,
                             BindingResult bindingResult,
                             @RequestParam("action") String action,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("editPostData", data);
            redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "editPostData", bindingResult);
            return "redirect:/edit-post/" + data.getId();
        }

        try {
            Long authorId = postService.updatePost(data, action);
            return "redirect:/profile/" + authorId;
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating post.");
            return "redirect:/edit-post/" + data.getId();
        }
    }

    @PostMapping("/delete-post/{id}")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            long postUserId = postService.deletePost(id);
            redirectAttributes.addFlashAttribute("successMessage", "Post deleted successfully.");
            return "redirect:/profile/" + postUserId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile";
        }
    }

    @GetMapping("/post/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Map<String, Object> postDetails = postService.getFullPostDetails(id);
        model.addAllAttributes(postDetails);

        return "display-post";
    }
}