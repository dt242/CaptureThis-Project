package com.project.capture_this.controller;

import com.project.capture_this.model.entity.User;
import com.project.capture_this.service.FollowService;
import com.project.capture_this.service.NotificationService;
import com.project.capture_this.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
    private final Long loggedInUserId = 1L;
    private final Long userToFollowId = 2L;

    @BeforeEach
    void setUp() {
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
    void testFollowUser_Success() throws Exception {
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
    void testFollowUser_WhenTargetIsSelf_ShouldNotCallServices() throws Exception {
        when(userService.getLoggedUser()).thenReturn(loggedInUser);

        mockMvc.perform(post("/profile/follow/{userId}", loggedInUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verifyNoInteractions(followService, notificationService);
    }

    @Test
    void testUnfollowUser_Success() throws Exception {
        when(userService.getLoggedUser()).thenReturn(loggedInUser);

        mockMvc.perform(post("/profile/unfollow/{userId}", userToFollowId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true));

        verify(followService, times(1)).unfollowUser(loggedInUserId, userToFollowId);
    }

    @Test
    void testUnfollowUser_WhenTargetIsSelf_ShouldNotCallService() throws Exception {
        when(userService.getLoggedUser()).thenReturn(loggedInUser);

        mockMvc.perform(post("/profile/unfollow/{userId}", loggedInUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verifyNoInteractions(followService);
    }

    @Test
    void testViewFollowers() throws Exception {
        List<User> followers = List.of(loggedInUser);
        when(followService.getFollowers(userToFollowId)).thenReturn(followers);

        mockMvc.perform(get("/followers/{userId}", userToFollowId))
                .andExpect(status().isOk())
                .andExpect(view().name("followers"))
                .andExpect(model().attributeExists("followers"))
                .andExpect(model().attribute("followers", org.hamcrest.Matchers.hasSize(1)));

        verify(followService, times(1)).getFollowers(userToFollowId);
    }

    @Test
    void testViewFollowing() throws Exception {
        List<User> following = List.of(loggedInUser);
        when(followService.getFollowing(userToFollowId)).thenReturn(following);

        mockMvc.perform(get("/following/{userId}", userToFollowId))
                .andExpect(status().isOk())
                .andExpect(view().name("following"))
                .andExpect(model().attributeExists("following"))
                .andExpect(model().attribute("following", org.hamcrest.Matchers.hasSize(1)));

        verify(followService, times(1)).getFollowing(userToFollowId);
    }
}