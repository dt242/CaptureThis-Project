package com.project.capture_this.controller;

import com.project.capture_this.model.dto.CommentDTO;
import com.project.capture_this.service.CommentService;
import com.project.capture_this.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
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
