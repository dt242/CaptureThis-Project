package com.project.capture_this.service;

import com.project.capture_this.model.dto.LikeDTO;
import com.project.capture_this.model.entity.Like;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.repository.LikeRepository;
import com.project.capture_this.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

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

    @InjectMocks
    private LikeService likeService;

    private User user;
    private Post post;
    private Like like;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");

        like = new Like();
        like.setId(1L);
        like.setUser(user);
        like.setPost(post);
    }

    @Test
    public void testLikePost() {
        when(likeRepository.existsByPostIdAndUserId(post.getId(), user.getId())).thenReturn(false);
        when(postRepository.findById(post.getId())).thenReturn(java.util.Optional.of(post));
        when(userService.findById(user.getId())).thenReturn(user);
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        likeService.likePost(post.getId(), user.getId());

        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    public void testLikePostAlreadyLiked() {
        when(likeRepository.existsByPostIdAndUserId(post.getId(), user.getId())).thenReturn(true);

        likeService.likePost(post.getId(), user.getId());

        verify(likeRepository, times(0)).save(any(Like.class));
    }

    @Test
    public void testUnlikePost() {
        when(likeRepository.existsByPostIdAndUserId(post.getId(), user.getId())).thenReturn(true);

        likeService.unlikePost(post.getId(), user.getId());

        verify(likeRepository, times(1)).deleteByPostIdAndUserId(post.getId(), user.getId());
    }

    @Test
    public void testUnlikePostNotLiked() {
        when(likeRepository.existsByPostIdAndUserId(post.getId(), user.getId())).thenReturn(false);

        likeService.unlikePost(post.getId(), user.getId());

        verify(likeRepository, times(0)).deleteByPostIdAndUserId(post.getId(), user.getId());
    }

    @Test
    public void testGetUsersWhoLikedPost() {
        List<Like> likes = new ArrayList<>();
        likes.add(like);
        when(likeRepository.findByPostId(post.getId())).thenReturn(likes);

        List<User> users = likeService.getUsersWhoLikedPost(post.getId());

        assertEquals(1, users.size());
        assertEquals(user.getId(), users.get(0).getId());
        verify(likeRepository, times(1)).findByPostId(post.getId());
    }

    @Test
    public void testIsUserLikedPost() {
        when(likeRepository.existsByPostIdAndUserId(post.getId(), user.getId())).thenReturn(true);

        boolean result = likeService.isUserLikedPost(post.getId(), user);

        assertTrue(result);
        verify(likeRepository, times(1)).existsByPostIdAndUserId(post.getId(), user.getId());
    }

    @Test
    public void testIsUserLikedPostNotLiked() {
        when(likeRepository.existsByPostIdAndUserId(post.getId(), user.getId())).thenReturn(false);

        boolean result = likeService.isUserLikedPost(post.getId(), user);

        assertFalse(result);
        verify(likeRepository, times(1)).existsByPostIdAndUserId(post.getId(), user.getId());
    }

    @Test
    public void testGetLikesByPostIdSortedDesc() {
        List<Like> likes = new ArrayList<>();
        likes.add(like);
        when(likeRepository.findAllByPostIdOrderByCreatedAtDesc(post.getId())).thenReturn(likes);

        List<Like> result = likeService.getLikesByPostIdSortedDesc(post.getId());

        assertEquals(1, result.size());
        assertEquals(like.getId(), result.get(0).getId());
        verify(likeRepository, times(1)).findAllByPostIdOrderByCreatedAtDesc(post.getId());
    }

    @Test
    public void testMapToLikeDTO() {
        LikeDTO likeDTO = LikeService.mapToLikeDTO(like);

        assertNotNull(likeDTO);
        assertEquals(like.getId(), likeDTO.getId());
        assertEquals(like.getUser().getId(), likeDTO.getUser().getId());
        assertEquals(like.getCreatedAt(), likeDTO.getCreatedAt());
    }
}
