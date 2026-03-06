package com.project.capture_this.service;

import com.project.capture_this.model.dto.LikeDTO;
import com.project.capture_this.model.entity.Like;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.repository.LikeRepository;
import com.project.capture_this.repository.PostRepository;
import com.project.capture_this.repository.UserRepository;
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
    private UserRepository userRepository;

    @InjectMocks
    private LikeService likeService;

    private User mockUser;
    private Post mockPost;
    private Like mockLike;

    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("Daniel");
        mockUser.setLastName("Boss");
        mockPost = new Post();
        mockPost.setId(100L);
        mockLike = new Like();
        mockLike.setId(500L);
        mockLike.setUser(mockUser);
        mockLike.setPost(mockPost);
        mockLike.setCreatedAt(LocalDateTime.now());
    }

    @Test
    public void testLikePost_Success() {
        when(likeRepository.existsByPostIdAndUserId(100L, 1L)).thenReturn(false);
        when(postRepository.findById(100L)).thenReturn(Optional.of(mockPost));
        when(userRepository.getReferenceById(1L)).thenReturn(mockUser);
        likeService.likePost(100L, 1L);

        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    public void testLikePost_WhenAlreadyLiked_ShouldNotSaveAgain() {
        when(likeRepository.existsByPostIdAndUserId(100L, 1L)).thenReturn(true);

        likeService.likePost(100L, 1L);

        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    public void testLikePost_WhenPostNotFound_ShouldThrowException() {
        when(likeRepository.existsByPostIdAndUserId(100L, 1L)).thenReturn(false);
        when(postRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.likePost(100L, 1L));
    }

    @Test
    public void testUnlikePost_ShouldInvokeDelete() {
        likeService.unlikePost(100L, 1L);
        verify(likeRepository, times(1)).deleteByPostIdAndUserId(100L, 1L);
    }

    @Test
    public void testGetUsersWhoLikedPost() {
        when(likeRepository.findByPostId(100L)).thenReturn(List.of(mockLike));
        List<User> users = likeService.getUsersWhoLikedPost(100L);

        assertEquals(1, users.size());
        assertEquals(1L, users.get(0).getId());
    }

    @Test
    public void testIsUserLikedPost() {
        when(likeRepository.existsByPostIdAndUserId(100L, 1L)).thenReturn(true);
        boolean result = likeService.isUserLikedPost(100L, mockUser);

        assertTrue(result);
    }

    @Test
    public void testMapToLikeDTO_ShouldMapCorrectly() {
        LikeDTO dto = LikeService.mapToLikeDTO(mockLike);

        assertNotNull(dto);
        assertEquals(500L, dto.getId());
        assertEquals(1L, dto.getUserId());
        assertEquals("Daniel", dto.getUserFirstName());
        assertEquals("Boss", dto.getUserLastName());
    }
}