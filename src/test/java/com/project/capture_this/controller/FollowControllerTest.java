package com.project.capture_this.controller;

import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.service.FollowService;
import com.project.capture_this.service.NotificationService;
import com.project.capture_this.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FollowControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private FollowService followService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private FollowController followController;

    private MockMvc mockMvc;

    private User loggedInUser;
    private User userToFollow;
    private Long loggedInUserId = 1L;
    private Long userToFollowId = 2L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(followController).build();

        loggedInUser = new User();
        loggedInUser.setId(loggedInUserId);
        loggedInUser.setFirstName("Test");
        loggedInUser.setLastName("User");

        userToFollow = new User();
        userToFollow.setId(userToFollowId);
        userToFollow.setFirstName("Followed");
        userToFollow.setLastName("User");
    }

    @Test
    void testFollowUser() throws Exception {
        when(userService.getLoggedUser()).thenReturn(loggedInUser);
        when(userService.findById(userToFollowId)).thenReturn(userToFollow);

        mockMvc.perform(post("/profile/follow/{userId}", userToFollowId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true));

        verify(followService, times(1)).followUser(loggedInUserId, userToFollowId);
        verify(notificationService, times(1)).notifyFollow(loggedInUser, userToFollow);
    }

    @Test
    void testUnfollowUser() throws Exception {
        when(userService.getLoggedUser()).thenReturn(loggedInUser);

        mockMvc.perform(post("/profile/unfollow/{userId}", userToFollowId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true));

        verify(followService, times(1)).unfollowUser(loggedInUserId, userToFollowId);
    }

    @Test
    void testViewFollowers() throws Exception {
        List<User> followers = Arrays.asList(loggedInUser);
        when(followService.getFollowers(userToFollowId)).thenReturn(followers);

        mockMvc.perform(get("/followers/{userId}", userToFollowId))
                .andExpect(status().isOk())
                .andExpect(view().name("followers"))
                .andExpect(model().attributeExists("followers"))
                .andExpect(model().attribute("followers",
                        org.hamcrest.Matchers.hasSize(1)));

        verify(followService, times(1)).getFollowers(userToFollowId);
    }

    @Test
    void testViewFollowing() throws Exception {
        List<User> following = Arrays.asList(loggedInUser);
        when(followService.getFollowing(userToFollowId)).thenReturn(following);

        mockMvc.perform(get("/following/{userId}", userToFollowId))
                .andExpect(status().isOk())
                .andExpect(view().name("following"))
                .andExpect(model().attributeExists("following"))
                .andExpect(model().attribute("following",
                        org.hamcrest.Matchers.hasSize(1)));

        verify(followService, times(1)).getFollowing(userToFollowId);
    }
}
