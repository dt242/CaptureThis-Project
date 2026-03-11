package com.project.capture_this.service;

import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.model.dto.LikeDTO;
import com.project.capture_this.model.entity.Like;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.repository.LikeRepository;
import com.project.capture_this.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LikeService likeService;

    private User loggedUser;
    private User postOwner;
    private Post mockPost;
    private Like mockLike;

    private final Long POST_ID = 100L;
    private final Long LOGGED_USER_ID = 1L;

    @BeforeEach
    public void setUp() {
        loggedUser = new User();
        loggedUser.setId(LOGGED_USER_ID);
        loggedUser.setFirstName("Daniel");
        loggedUser.setLastName("Boss");
        postOwner = new User();
        postOwner.setId(2L);
        postOwner.setFirstName("Other");
        mockPost = new Post();
        mockPost.setId(POST_ID);
        mockPost.setUser(postOwner);
        mockLike = new Like();
        mockLike.setId(500L);
        mockLike.setUser(loggedUser);
        mockLike.setPost(mockPost);
        mockLike.setCreatedAt(LocalDateTime.now());
    }

    @Test
    public void testLikePost_Success_ShouldNotifyPostOwner() {
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(likeRepository.existsByPostIdAndUserId(POST_ID, LOGGED_USER_ID)).thenReturn(false);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(mockPost));
        likeService.likePost(POST_ID);

        verify(likeRepository, times(1)).save(any(Like.class));
        verify(notificationService, times(1)).notifyLike(loggedUser, mockPost);
    }

    @Test
    public void testLikePost_WhenLikingOwnPost_ShouldNotNotify() {
        mockPost.setUser(loggedUser);
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(likeRepository.existsByPostIdAndUserId(POST_ID, LOGGED_USER_ID)).thenReturn(false);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(mockPost));
        likeService.likePost(POST_ID);

        verify(likeRepository, times(1)).save(any(Like.class));
        verify(notificationService, never()).notifyLike(any(), any());
    }

    @Test
    public void testLikePost_WhenAlreadyLiked_ShouldNotSaveAgain() {
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(likeRepository.existsByPostIdAndUserId(POST_ID, LOGGED_USER_ID)).thenReturn(true);
        likeService.likePost(POST_ID);

        verify(likeRepository, never()).save(any(Like.class));
        verify(notificationService, never()).notifyLike(any(), any());
    }

    @Test
    public void testLikePost_WhenPostNotFound_ShouldThrowException() {
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(likeRepository.existsByPostIdAndUserId(POST_ID, LOGGED_USER_ID)).thenReturn(false);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.likePost(POST_ID));
    }

    @Test
    public void testUnlikePost_ShouldInvokeDelete() {
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        likeService.unlikePost(POST_ID);

        verify(likeRepository, times(1)).deleteByPostIdAndUserId(POST_ID, LOGGED_USER_ID);
    }

    @Test
    public void testGetUsersWhoLikedPostDTOs() {
        when(likeRepository.findByPostId(POST_ID)).thenReturn(List.of(mockLike));

        List<DisplayUserDTO> users = likeService.getUsersWhoLikedPostDTOs(POST_ID);

        assertEquals(1, users.size());
        assertEquals(LOGGED_USER_ID, users.get(0).getId());
        assertEquals("Daniel Boss", users.get(0).getFirstName() + " " + users.get(0).getLastName());
    }

    @Test
    public void testIsUserLikedPost() {
        when(likeRepository.existsByPostIdAndUserId(POST_ID, LOGGED_USER_ID)).thenReturn(true);
        boolean result = likeService.isUserLikedPost(POST_ID, loggedUser);

        assertTrue(result);
    }

    @Test
    public void testGetLikesByPostIdSortedDesc() {
        when(likeRepository.findAllByPostIdOrderByCreatedAtDesc(POST_ID)).thenReturn(List.of(mockLike));
        List<Like> likes = likeService.getLikesByPostIdSortedDesc(POST_ID);

        assertEquals(1, likes.size());
        assertEquals(500L, likes.get(0).getId());
    }

    @Test
    public void testMapToLikeDTO_ShouldMapCorrectly() {
        LikeDTO dto = LikeService.mapToLikeDTO(mockLike);

        assertNotNull(dto);
        assertEquals(500L, dto.getId());
        assertEquals(LOGGED_USER_ID, dto.getUserId());
        assertEquals("Daniel", dto.getUserFirstName());
        assertEquals("Boss", dto.getUserLastName());
    }
}