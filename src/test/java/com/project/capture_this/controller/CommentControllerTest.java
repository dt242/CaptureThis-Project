package com.project.capture_this.controller;

import com.project.capture_this.model.dto.CommentDTO;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.service.CommentService;
import com.project.capture_this.service.NotificationService;
import com.project.capture_this.service.PostService;
import com.project.capture_this.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PostService postService;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mockMvc;

    private User loggedUser;
    private User otherUser;
    private Post mockPost;
    private final Long POST_ID = 100L;
    private final Long COMMENT_ID = 5L;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();

        loggedUser = new User();
        loggedUser.setId(1L);
        loggedUser.setUsername("dani_the_boss");
        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("regular_guy");
        mockPost = new Post();
        mockPost.setId(POST_ID);
        mockPost.setUser(otherUser);
    }

    @Test
    void testAddComment_Success_ShouldNotifyPostOwner() throws Exception {
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(postService.findById(POST_ID)).thenReturn(mockPost);

        mockMvc.perform(post("/post/{postId}/comment", POST_ID)
                        .param("commentText", "Awesome picture!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + POST_ID));

        verify(commentService, times(1)).addComment(any(CommentDTO.class));
        verify(notificationService, times(1)).notifyComment(loggedUser, mockPost);
    }

    @Test
    void testAddComment_WhenCommentingOwnPost_ShouldNotNotify() throws Exception {
        when(userService.getLoggedUser()).thenReturn(loggedUser);

        mockPost.setUser(loggedUser);
        when(postService.findById(POST_ID)).thenReturn(mockPost);

        mockMvc.perform(post("/post/{postId}/comment", POST_ID)
                        .param("commentText", "Just replying to my own post."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + POST_ID));

        verify(commentService, times(1)).addComment(any(CommentDTO.class));
        verify(notificationService, never()).notifyComment(any(), any());
    }

    @Test
    void testAddComment_WhenExceptionThrown_ShouldReturnErrorMessage() throws Exception {
        when(userService.getLoggedUser()).thenReturn(loggedUser);

        doThrow(new RuntimeException("Database error")).when(commentService).addComment(any(CommentDTO.class));

        mockMvc.perform(post("/post/{postId}/comment", POST_ID)
                        .param("commentText", "This will fail"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + POST_ID))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(notificationService, never()).notifyComment(any(), any());
    }

    @Test
    void testDeleteComment_Success_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/post/{postId}/comment/{commentId}", POST_ID, COMMENT_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + POST_ID))
                .andExpect(flash().attributeExists("successMessage"));

        verify(commentService, times(1)).deleteComment(COMMENT_ID);
    }

    @Test
    void testDeleteComment_WhenExceptionThrown_ShouldReturnErrorMessage() throws Exception {
        doThrow(new RuntimeException("Comment not found")).when(commentService).deleteComment(COMMENT_ID);

        mockMvc.perform(delete("/post/{postId}/comment/{commentId}", POST_ID, COMMENT_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + POST_ID))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}