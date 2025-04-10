package com.project.capture_this.service;

import com.project.capture_this.model.entity.User;
import com.project.capture_this.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FollowService followService;

    private User follower;
    private User followee;

    @BeforeEach
    public void setUp() {
        follower = new User();
        follower.setId(1L);
        follower.setUsername("follower");

        followee = new User();
        followee.setId(2L);
        followee.setUsername("followee");
    }

    @Test
    public void testFollowUser() {
        when(userRepository.findById(follower.getId())).thenReturn(java.util.Optional.of(follower));
        when(userRepository.findById(followee.getId())).thenReturn(java.util.Optional.of(followee));
        when(userRepository.save(follower)).thenReturn(follower);
        when(userRepository.save(followee)).thenReturn(followee);

        followService.followUser(follower.getId(), followee.getId());

        assertTrue(follower.getFollowing().contains(followee));
        assertTrue(followee.getFollowers().contains(follower));
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    public void testFollowUserAlreadyFollowing() {
        follower.getFollowing().add(followee);
        followee.getFollowers().add(follower);

        when(userRepository.findById(follower.getId())).thenReturn(java.util.Optional.of(follower));
        when(userRepository.findById(followee.getId())).thenReturn(java.util.Optional.of(followee));

        followService.followUser(follower.getId(), followee.getId());

        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void testUnfollowUser() {
        follower.getFollowing().add(followee);
        followee.getFollowers().add(follower);

        when(userRepository.findById(follower.getId())).thenReturn(java.util.Optional.of(follower));
        when(userRepository.findById(followee.getId())).thenReturn(java.util.Optional.of(followee));
        when(userRepository.save(follower)).thenReturn(follower);
        when(userRepository.save(followee)).thenReturn(followee);

        followService.unfollowUser(follower.getId(), followee.getId());

        assertFalse(follower.getFollowing().contains(followee));
        assertFalse(followee.getFollowers().contains(follower));
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    public void testUnfollowUserNotFollowing() {
        when(userRepository.findById(follower.getId())).thenReturn(java.util.Optional.of(follower));
        when(userRepository.findById(followee.getId())).thenReturn(java.util.Optional.of(followee));

        followService.unfollowUser(follower.getId(), followee.getId());

        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void testGetFollowers() {
        follower.getFollowers().add(followee);

        when(userRepository.findById(follower.getId())).thenReturn(java.util.Optional.of(follower));

        List<User> followers = followService.getFollowers(follower.getId());

        assertEquals(1, followers.size());
        assertEquals(followee.getId(), followers.get(0).getId());
        verify(userRepository, times(1)).findById(follower.getId());
    }

    @Test
    public void testGetFollowing() {
        follower.getFollowing().add(followee);

        when(userRepository.findById(follower.getId())).thenReturn(java.util.Optional.of(follower));

        List<User> following = followService.getFollowing(follower.getId());

        assertEquals(1, following.size());
        assertEquals(followee.getId(), following.get(0).getId());
        verify(userRepository, times(1)).findById(follower.getId());
    }

    @Test
    public void testIsFollowing() {
        when(userRepository.findById(follower.getId())).thenReturn(java.util.Optional.of(follower));
        when(userRepository.findById(followee.getId())).thenReturn(java.util.Optional.of(followee));

        follower.getFollowing().add(followee);

        boolean result = followService.isFollowing(follower.getId(), followee.getId());

        assertTrue(result);
        verify(userRepository, times(1)).findById(follower.getId());
        verify(userRepository, times(1)).findById(followee.getId());
    }

    @Test
    public void testIsNotFollowing() {
        when(userRepository.findById(follower.getId())).thenReturn(java.util.Optional.of(follower));
        when(userRepository.findById(followee.getId())).thenReturn(java.util.Optional.of(followee));

        boolean result = followService.isFollowing(follower.getId(), followee.getId());

        assertFalse(result);
        verify(userRepository, times(1)).findById(follower.getId());
        verify(userRepository, times(1)).findById(followee.getId());
    }
}
