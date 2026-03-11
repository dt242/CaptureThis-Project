package com.project.capture_this.controller;

import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.service.FollowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FollowControllerTest {

    @Mock
    private FollowService followService;

    @InjectMocks
    private FollowController followController;

    private MockMvc mockMvc;

    private final Long TARGET_USER_ID = 2L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(followController).build();
    }

    @Test
    void testFollowUser_Success() throws Exception {
        mockMvc.perform(post("/profile/follow/{userId}", TARGET_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true));

        verify(followService, times(1)).followUser(TARGET_USER_ID);
    }

    @Test
    void testUnfollowUser_Success() throws Exception {
        mockMvc.perform(post("/profile/unfollow/{userId}", TARGET_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true));

        verify(followService, times(1)).unfollowUser(TARGET_USER_ID);
    }

    @Test
    void testViewFollowers() throws Exception {
        List<DisplayUserDTO> followers = List.of(new DisplayUserDTO());
        when(followService.getFollowersDTOs(TARGET_USER_ID)).thenReturn(followers);

        mockMvc.perform(get("/followers/{userId}", TARGET_USER_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("followers"))
                .andExpect(model().attributeExists("followers"))
                .andExpect(model().attribute("followers", org.hamcrest.Matchers.hasSize(1)));

        verify(followService, times(1)).getFollowersDTOs(TARGET_USER_ID);
    }

    @Test
    void testViewFollowing() throws Exception {
        List<DisplayUserDTO> following = List.of(new DisplayUserDTO());
        when(followService.getFollowingDTOs(TARGET_USER_ID)).thenReturn(following);

        mockMvc.perform(get("/following/{userId}", TARGET_USER_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("following"))
                .andExpect(model().attributeExists("following"))
                .andExpect(model().attribute("following", org.hamcrest.Matchers.hasSize(1)));

        verify(followService, times(1)).getFollowingDTOs(TARGET_USER_ID);
    }
}