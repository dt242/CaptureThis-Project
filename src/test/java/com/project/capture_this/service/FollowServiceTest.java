package com.project.capture_this.service;

import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private FollowService followService;

    private User follower;
    private User followee;
    private final Long FOLLOWER_ID = 1L;
    private final Long FOLLOWEE_ID = 2L;

    @BeforeEach
    public void setUp() {
        follower = new User();
        follower.setId(FOLLOWER_ID);
        follower.setUsername("follower");
        follower.setFollowing(new HashSet<>());
        follower.setFollowers(new HashSet<>());
        followee = new User();
        followee.setId(FOLLOWEE_ID);
        followee.setUsername("followee");
        followee.setFollowing(new HashSet<>());
        followee.setFollowers(new HashSet<>());
    }

    @Test
    public void testFollowUser_Success() {
        when(userService.getLoggedUser()).thenReturn(follower);
        when(userRepository.findById(FOLLOWEE_ID)).thenReturn(Optional.of(followee));
        followService.followUser(FOLLOWEE_ID);

        assertTrue(follower.getFollowing().contains(followee));
        assertTrue(followee.getFollowers().contains(follower));
        verify(notificationService, times(1)).notifyFollow(follower, followee);
    }

    @Test
    public void testFollowUser_WhenAlreadyFollowing_ShouldDoNothing() {
        follower.getFollowing().add(followee);
        followee.getFollowers().add(follower);
        when(userService.getLoggedUser()).thenReturn(follower);
        when(userRepository.findById(FOLLOWEE_ID)).thenReturn(Optional.of(followee));
        followService.followUser(FOLLOWEE_ID);

        assertEquals(1, follower.getFollowing().size());
        assertEquals(1, followee.getFollowers().size());
        verify(notificationService, never()).notifyFollow(any(), any());
    }

    @Test
    public void testFollowUser_WhenFollowingSelf_ShouldThrowException() {
        when(userService.getLoggedUser()).thenReturn(follower);

        assertThrows(IllegalArgumentException.class, () -> {
            followService.followUser(FOLLOWER_ID);
        });

        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    public void testUnfollowUser_Success() {
        follower.getFollowing().add(followee);
        followee.getFollowers().add(follower);
        when(userService.getLoggedUser()).thenReturn(follower);
        when(userRepository.findById(FOLLOWEE_ID)).thenReturn(Optional.of(followee));
        followService.unfollowUser(FOLLOWEE_ID);

        assertFalse(follower.getFollowing().contains(followee));
        assertFalse(followee.getFollowers().contains(follower));
    }

    @Test
    public void testUnfollowUser_WhenNotFollowing_ShouldDoNothing() {
        when(userService.getLoggedUser()).thenReturn(follower);
        when(userRepository.findById(FOLLOWEE_ID)).thenReturn(Optional.of(followee));
        followService.unfollowUser(FOLLOWEE_ID);

        assertTrue(follower.getFollowing().isEmpty());
    }

    @Test
    public void testUnfollowUser_WhenUnfollowingSelf_ShouldThrowException() {
        when(userService.getLoggedUser()).thenReturn(follower);

        assertThrows(IllegalArgumentException.class, () -> {
            followService.unfollowUser(FOLLOWER_ID);
        });
    }

    @Test
    public void testGetFollowersDTOs_Success() {
        follower.getFollowers().add(followee);
        when(userRepository.findById(FOLLOWER_ID)).thenReturn(Optional.of(follower));
        List<DisplayUserDTO> followersList = followService.getFollowersDTOs(FOLLOWER_ID);

        assertEquals(1, followersList.size());
        assertEquals(FOLLOWEE_ID, followersList.get(0).getId());
    }

    @Test
    public void testGetFollowersDTOs_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            followService.getFollowersDTOs(99L);
        });
    }

    @Test
    public void testGetFollowingDTOs_Success() {
        follower.getFollowing().add(followee);
        when(userRepository.findById(FOLLOWER_ID)).thenReturn(Optional.of(follower));
        List<DisplayUserDTO> followingList = followService.getFollowingDTOs(FOLLOWER_ID);

        assertEquals(1, followingList.size());
        assertEquals(FOLLOWEE_ID, followingList.get(0).getId());
    }

    @Test
    public void testIsFollowing_WhenTrue_ShouldReturnTrue() {
        follower.getFollowing().add(followee);
        when(userRepository.findById(FOLLOWER_ID)).thenReturn(Optional.of(follower));
        when(userRepository.findById(FOLLOWEE_ID)).thenReturn(Optional.of(followee));

        assertTrue(followService.isFollowing(FOLLOWER_ID, FOLLOWEE_ID));
    }

    @Test
    public void testIsFollowing_WhenFalse_ShouldReturnFalse() {
        when(userRepository.findById(FOLLOWER_ID)).thenReturn(Optional.of(follower));
        when(userRepository.findById(FOLLOWEE_ID)).thenReturn(Optional.of(followee));

        assertFalse(followService.isFollowing(FOLLOWER_ID, FOLLOWEE_ID));
    }
}