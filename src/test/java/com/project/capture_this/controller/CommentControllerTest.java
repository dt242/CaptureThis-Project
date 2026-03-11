package com.project.capture_this.controller;

import com.project.capture_this.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mockMvc;

    private final Long POST_ID = 100L;
    private final Long COMMENT_ID = 5L;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Test
    void testAddComment_Success_ShouldRedirectToPost() throws Exception {
        String commentText = "Awesome picture!";

        mockMvc.perform(post("/post/{postId}/comment", POST_ID)
                        .param("commentText", commentText))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + POST_ID));

        verify(commentService, times(1)).addComment(POST_ID, commentText);
    }

    @Test
    void testAddComment_WhenExceptionThrown_ShouldReturnErrorMessage() throws Exception {
        String commentText = "This will fail";
        doThrow(new RuntimeException("Database error")).when(commentService).addComment(POST_ID, commentText);

        mockMvc.perform(post("/post/{postId}/comment", POST_ID)
                        .param("commentText", commentText))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + POST_ID))
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage", "Could not add comment: Database error"));
    }

    @Test
    void testDeleteComment_Success_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/post/{postId}/comment/{commentId}", POST_ID, COMMENT_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + POST_ID))
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(flash().attribute("successMessage", "Comment deleted successfully."));

        verify(commentService, times(1)).deleteComment(COMMENT_ID);
    }

    @Test
    void testDeleteComment_WhenExceptionThrown_ShouldReturnErrorMessage() throws Exception {
        doThrow(new RuntimeException("Comment not found")).when(commentService).deleteComment(COMMENT_ID);

        mockMvc.perform(delete("/post/{postId}/comment/{commentId}", POST_ID, COMMENT_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + POST_ID))
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage", "Error deleting comment: Comment not found"));
    }
}