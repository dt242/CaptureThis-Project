package com.project.capture_this.controller;

import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    @InjectMocks
    private LikeRestController likeRestController;

    private MockMvc mockMvc;

    private final Long POST_ID = 100L;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeRestController).build();
    }

    @Test
    void testLikePost_Success() throws Exception {
        mockMvc.perform(post("/post/like/{postId}", POST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(likeService, times(1)).likePost(POST_ID);
    }

    @Test
    void testLikePost_WhenExceptionThrown_ShouldReturnBadRequest() throws Exception {
        doThrow(new RuntimeException("Post not found")).when(likeService).likePost(POST_ID);

        mockMvc.perform(post("/post/like/{postId}", POST_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Post not found"));
    }

    @Test
    void testUnlikePost_Success() throws Exception {
        mockMvc.perform(post("/post/unlike/{postId}", POST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(likeService, times(1)).unlikePost(POST_ID);
    }

    @Test
    void testUnlikePost_WhenExceptionThrown_ShouldReturnBadRequest() throws Exception {
        doThrow(new RuntimeException("Database down")).when(likeService).unlikePost(POST_ID);

        mockMvc.perform(post("/post/unlike/{postId}", POST_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Database down"));
    }

    @Test
    void testGetLikes_Success() throws Exception {
        DisplayUserDTO dto = new DisplayUserDTO();
        List<DisplayUserDTO> mockUsers = List.of(dto);
        when(likeService.getUsersWhoLikedPostDTOs(POST_ID)).thenReturn(mockUsers);

        mockMvc.perform(get("/post/likes/{postId}", POST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.likes.length()").value(1));

        verify(likeService, times(1)).getUsersWhoLikedPostDTOs(POST_ID);
    }

    @Test
    void testGetLikes_WhenNoLikes_ShouldReturnEmptyList() throws Exception {
        when(likeService.getUsersWhoLikedPostDTOs(POST_ID)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/post/likes/{postId}", POST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.likes.length()").value(0));

        verify(likeService, times(1)).getUsersWhoLikedPostDTOs(POST_ID);
    }
}