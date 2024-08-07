package com.project.capture_this.controller;

import com.project.capture_this.model.dto.CommentDTO;
import com.project.capture_this.model.dto.CreatePostDTO;
import com.project.capture_this.model.dto.DisplayPostDTO;
import com.project.capture_this.model.dto.EditPostDTO;
import com.project.capture_this.model.entity.Comment;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.PostStatus;
import com.project.capture_this.service.CommentService;
import com.project.capture_this.service.PostService;
import com.project.capture_this.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;

    public PostController(PostService postService, CommentService commentService, UserService userService) {
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
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
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.createPostData", bindingResult);
            return "redirect:/create-post";
        }

        try {
            // Determine the status based on the action
            PostStatus status = "post".equals(action) ? PostStatus.PUBLISHED : PostStatus.DRAFT;
            postService.savePost(data, status);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error appropriately
        }

        return "redirect:/profile";
    }

    @GetMapping("/images/{postId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long postId) {
        Post post = postService.findById(postId);
        byte[] image = post.getImage();

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }

    @GetMapping("/drafts")
    public String viewDrafts(Model model) {
        List<DisplayPostDTO> drafts = postService.findDraftPosts();
        model.addAttribute("drafts", drafts);
        return "drafts";
    }

    @GetMapping("/favorites")
    public String viewFavorites(Model model) {
        List<DisplayPostDTO> favorites = postService.findFavoritePosts();
        model.addAttribute("favorites", favorites);
        return "favorites"; // Ensure this matches the name of your Thymeleaf template
    }

    @GetMapping("/edit-post/{id}")
    public String viewEditPost(@PathVariable Long id, Model model) {
        Post post = postService.findById(id);
        EditPostDTO dto = EditPostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .description(post.getDescription())
                .build();
        model.addAttribute("editPostData", dto);
        return "edit-post"; // Ensure this matches the name of your Thymeleaf template
    }

    @PostMapping("/edit-post")
    public String doEditPost(@Valid @ModelAttribute("editPostData") EditPostDTO data,
                             BindingResult bindingResult,
                             @RequestParam("action") String action,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("editPostData", data);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editPostData", bindingResult);
            return "redirect:/edit-post/" + data.getId();
        }

        try {
            postService.updatePost(data);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error appropriately
        }

        return "redirect:/profile";
    }

    @GetMapping("/delete-post/{id}")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            postService.deletePost(id);
            redirectAttributes.addFlashAttribute("successMessage", "Post deleted successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile"; // or wherever you want to redirect after deletion
    }

    @GetMapping("/post/{postId}/comments")
    public List<CommentDTO> getComments(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @PostMapping("/post/{postId}/comment")
    public String addComment(@PathVariable Long postId,
                             @RequestParam("commentText") String commentText,
//                             @RequestParam("userId") Long userId,
                             RedirectAttributes redirectAttributes) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setPostId(postId);
        commentDTO.setUserId(userService.getLoggedUser().getId());
        commentDTO.setContent(commentText);
        try {
            commentService.addComment(commentDTO);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not add comment: " + e.getMessage());
        }
        return "redirect:/post/" + postId;
    }

    @PutMapping("/comment/{commentId}")
    public CommentDTO updateComment(@PathVariable Long commentId, @RequestBody String content) {
        return commentService.updateComment(commentId, content);
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }

    @GetMapping("/post/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.findById(id);
        List<CommentDTO> comments = commentService.getCommentsByPostId(id);
        List<User> likes = postService.getUsersWhoLikedPost(id);

        boolean isLiked = postService.isUserLikedPost(id, userService.getLoggedUser());

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("likes", likes);
        model.addAttribute("isLiked", isLiked);

        return "display-post";
    }

    @PostMapping("/post/like/{postId}")
    public ResponseEntity<Map<String, Object>> likePost(@PathVariable Long postId) {
        Map<String, Object> response = new HashMap<>();
        try {
            postService.likePost(postId, userService.getLoggedUser().getId());
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/post/unlike/{postId}")
    public ResponseEntity<Map<String, Object>> unlikePost(@PathVariable Long postId) {
        Map<String, Object> response = new HashMap<>();
        try {
            postService.unlikePost(postId, userService.getLoggedUser().getId());
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

}
