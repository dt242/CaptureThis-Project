package com.project.capture_this.controller;

import com.project.capture_this.model.dto.CommentDTO;
import com.project.capture_this.model.dto.CreatePostDTO;
import com.project.capture_this.model.dto.DisplayPostDTO;
import com.project.capture_this.model.dto.EditPostDTO;
import com.project.capture_this.model.entity.Like;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.PostStatus;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
//    private final FavoriteService favoriteService;
    private final LikeService likeService;

    public PostController(PostService postService, CommentService commentService, UserService userService, LikeService likeService) {
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
//        this.favoriteService = favoriteService;
        this.likeService = likeService;
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
            PostStatus status = "post".equals(action) ? PostStatus.PUBLISHED : PostStatus.DRAFT;
            postService.savePost(data, status);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error uploading image. Please try again.");
            return "redirect:/create-post";
        }

        return "redirect:/profile";
    }

    @GetMapping("/images/{postId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long postId) {
        Post post = postService.findById(postId);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(post.getImage());
    }

    @GetMapping("/drafts")
    public String viewDrafts(Model model) {
        List<DisplayPostDTO> drafts = postService.findDraftPosts();
        model.addAttribute("drafts", drafts);
        return "drafts";
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
        model.addAttribute("isAlreadyPosted", post.getStatus().equals(PostStatus.PUBLISHED));
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
            PostStatus status = "post".equals(action) ? PostStatus.PUBLISHED : PostStatus.DRAFT;
            postService.updatePost(data, status);
            return "redirect:/profile";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating post.");
            return "redirect:/edit-post/" + data.getId();
        }
    }

    @PostMapping("/delete-post/{id}")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            long postUserId = postService.findById(id).getUser().getId();
            postService.deletePost(id);
            redirectAttributes.addFlashAttribute("successMessage", "Post deleted successfully.");
            return "redirect:/profile/" + postUserId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile";
        }
    }

    @GetMapping("/post/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.findById(id);

        User loggedUser = userService.getLoggedUser();
        List<CommentDTO> comments = commentService.getCommentsByPostId(id).stream()
                .sorted(Comparator.comparing(CommentDTO::getCreatedAt).reversed())
                .peek(comment -> comment.setOwnComment(comment.getUserId().equals(loggedUser.getId())))
                .toList();

        Map<Long, List<User>> commentAuthors = comments.stream()
                .map(comment -> userService.findById(comment.getUserId()))
                .collect(Collectors.groupingBy(User::getId));

        List<Like> likes = likeService.getLikesByPostIdSortedDesc(id);

        boolean isLiked = likeService.isUserLikedPost(id, loggedUser);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("commentAuthors", commentAuthors);
        model.addAttribute("likes", likes);
        model.addAttribute("isLiked", isLiked);
        model.addAttribute("isAdmin", loggedUser.isAdmin());

        return "display-post";
    }
}