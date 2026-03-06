package com.project.capture_this.controller;

import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.service.LikeService;
import com.project.capture_this.service.NotificationService;
import com.project.capture_this.service.PostService;
import com.project.capture_this.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LikeRestControllerTest {

    @Mock
    private LikeService likeService;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PostService postService;

    @InjectMocks
    private LikeRestController likeRestController;

    private MockMvc mockMvc;

    private User loggedInUser;
    private User otherUser;
    private Post mockPost;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeRestController).build();

        loggedInUser = new User();
        loggedInUser.setId(1L);
        loggedInUser.setUsername("loggedInUser");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("otherUser");

        mockPost = new Post();
        mockPost.setId(100L);
        mockPost.setUser(otherUser);
    }

    @Test
    void testLikePost_Success() throws Exception {
        when(userService.getLoggedUser()).thenReturn(loggedInUser);
        when(postService.findById(100L)).thenReturn(mockPost);
        mockMvc.perform(post("/post/like/{postId}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(likeService).likePost(100L, loggedInUser.getId());
        verify(notificationService).notifyLike(loggedInUser, mockPost);
    }

    @Test
    void testLikePost_WhenPostOwnerIsSelf_ShouldNotNotify() throws Exception {
        mockPost.setUser(loggedInUser);

        when(userService.getLoggedUser()).thenReturn(loggedInUser);
        when(postService.findById(100L)).thenReturn(mockPost);

        mockMvc.perform(post("/post/like/{postId}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(likeService).likePost(100L, loggedInUser.getId());
        verify(notificationService, never()).notifyLike(any(), any());
    }

    @Test
    void testLikePost_WhenPostNotFound_ShouldReturnErrorJson() throws Exception {
        when(userService.getLoggedUser()).thenReturn(loggedInUser);
        when(postService.findById(100L)).thenThrow(new EntityNotFoundException("Post not found"));

        mockMvc.perform(post("/post/like/{postId}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Post not found"));

        verify(notificationService, never()).notifyLike(any(), any());
    }

    @Test
    void testUnlikePost_Success() throws Exception {
        when(userService.getLoggedUser()).thenReturn(loggedInUser);

        mockMvc.perform(post("/post/unlike/{postId}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(likeService).unlikePost(100L, loggedInUser.getId());
    }

    @Test
    void testUnlikePost_WhenExceptionThrown_ShouldReturnErrorJson() throws Exception {
        when(userService.getLoggedUser()).thenReturn(loggedInUser);
        doThrow(new RuntimeException("Database down")).when(likeService).unlikePost(100L, loggedInUser.getId());

        mockMvc.perform(post("/post/unlike/{postId}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Database down"));
    }

    @Test
    void testGetLikes_Success() throws Exception {
        List<User> mockUsers = List.of(loggedInUser);
        when(likeService.getUsersWhoLikedPost(100L)).thenReturn(mockUsers);

        mockMvc.perform(get("/post/likes/{postId}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.likes.length()").value(1));

        verify(likeService).getUsersWhoLikedPost(100L);
    }

    @Test
    void testGetLikes_WhenNoLikes_ShouldReturnEmptyList() throws Exception {
        when(likeService.getUsersWhoLikedPost(100L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/post/likes/{postId}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.likes.length()").value(0));

        verify(likeService).getUsersWhoLikedPost(100L);
    }
}