package com.project.capture_this.controller;

import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.service.LikeService;
import com.project.capture_this.service.NotificationService;
import com.project.capture_this.service.PostService;
import com.project.capture_this.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

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

    private User mockUser;
    private Post mockPost;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(likeRestController).build();

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("mockUser");

        mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setUser(mockUser);
    }

    @Test
    void testLikePost() throws Exception {
        when(userService.getLoggedUser()).thenReturn(mockUser);
        when(postService.findById(1L)).thenReturn(mockPost);

        User postOwner = new User();
        postOwner.setId(2L);
        mockPost.setUser(postOwner);

        doNothing().when(likeService).likePost(1L, mockUser.getId());

        mockMvc.perform(post("/post/like/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(likeService).likePost(1L, mockUser.getId());
        verify(notificationService).notifyLike(mockUser, mockPost);
    }

    @Test
    void testLikePostWhenPostOwnerIsUser() throws Exception {
        mockPost.setUser(mockUser);
        when(userService.getLoggedUser()).thenReturn(mockUser);
        when(postService.findById(1L)).thenReturn(mockPost);
        doNothing().when(likeService).likePost(1L, mockUser.getId());

        mockMvc.perform(post("/post/like/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(likeService).likePost(1L, mockUser.getId());
        verify(notificationService, never()).notifyLike(any(), any());
    }

    @Test
    void testUnlikePost() throws Exception {
        when(userService.getLoggedUser()).thenReturn(mockUser);
        doNothing().when(likeService).unlikePost(1L, mockUser.getId());

        mockMvc.perform(post("/post/unlike/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(likeService).unlikePost(1L, mockUser.getId());
    }

    @Test
    void testGetLikes() throws Exception {
        List<User> mockUsers = Arrays.asList(mockUser);
        when(likeService.getUsersWhoLikedPost(1L)).thenReturn(mockUsers);

        mockMvc.perform(get("/post/likes/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.likes.length()").value(1));

        verify(likeService).getUsersWhoLikedPost(1L);
    }

    @Test
    void testGetLikesWhenNoLikes() throws Exception {
        when(likeService.getUsersWhoLikedPost(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/post/likes/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.likes.length()").value(0));

        verify(likeService).getUsersWhoLikedPost(1L);
    }
}
