package com.project.capture_this.controller;

import com.project.capture_this.model.dto.CommentDTO;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.service.CommentService;
import com.project.capture_this.service.NotificationService;
import com.project.capture_this.service.PostService;
import com.project.capture_this.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final PostService postService;

    public CommentController(CommentService commentService, UserService userService, NotificationService notificationService, PostService postService) {
        this.commentService = commentService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.postService = postService;
    }

    @GetMapping("/post/{postId}/comments")
    public List<CommentDTO> getComments(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @PostMapping("/post/{postId}/comment")
    public String addComment(@PathVariable Long postId,
                             @RequestParam("commentText") String commentText,
                             RedirectAttributes redirectAttributes) {
        CommentDTO commentDTO = new CommentDTO();
        User loggedUser = userService.getLoggedUser();

        commentDTO.setPostId(postId);
        commentDTO.setUserId(loggedUser.getId());
        commentDTO.setContent(commentText);

        try {
            commentService.addComment(commentDTO);

            Post commentedPost = postService.findById(postId);
            if (!commentedPost.getUser().equals(loggedUser)) {
                notificationService.notifyComment(loggedUser, commentedPost);
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not add comment: " + e.getMessage());
        }
        return "redirect:/post/" + postId;
    }


    @DeleteMapping("/post/{postId}/comment/{commentId}")
    public String deleteComment(@PathVariable Long postId,
                                @PathVariable Long commentId,
                                RedirectAttributes redirectAttributes) {
        try {
            commentService.deleteComment(commentId);
            redirectAttributes.addFlashAttribute("successMessage", "Comment deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting comment: " + e.getMessage());
        }
        return "redirect:/post/" + postId;
    }
}
