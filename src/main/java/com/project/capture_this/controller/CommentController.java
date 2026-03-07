package com.project.capture_this.controller;

import com.project.capture_this.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/post/{postId}/comment")
    public String addComment(@PathVariable Long postId,
                             @RequestParam("commentText") String commentText,
                             RedirectAttributes redirectAttributes) {

        try {
            commentService.addComment(postId, commentText);
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
