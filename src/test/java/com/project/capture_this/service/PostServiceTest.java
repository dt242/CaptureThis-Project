package com.project.capture_this.service;

import com.project.capture_this.model.dto.CreatePostDTO;
import com.project.capture_this.model.dto.DisplayPostDTO;
import com.project.capture_this.model.dto.EditPostDTO;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.PostStatus;
import com.project.capture_this.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private PostService postService;

    private User mockUser;
    private Post mockPost;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("Daniel");
        mockUser.setLastName("Boss");
        mockUser.setFollowing(new HashSet<>());
        mockPost = new Post();
        mockPost.setId(100L);
        mockPost.setTitle("Original Title");
        mockPost.setDescription("Original Description");
        mockPost.setUser(mockUser);
        mockPost.setStatus(PostStatus.PUBLISHED);
        mockPost.setLikes(new HashSet<>());
    }

    @Test
    void testFindById_Success() {
        when(postRepository.findById(100L)).thenReturn(Optional.of(mockPost));
        Post result = postService.findById(100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
    }

    @Test
    void testFindById_NotFound_ShouldThrowException() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.findById(999L));
    }

    @Test
    void testFindFollowedPosts_WhenNoFollowing_ShouldReturnEmptyList() {
        when(userService.getLoggedUser()).thenReturn(mockUser);
        List<DisplayPostDTO> result = postService.findFollowedPosts();

        assertTrue(result.isEmpty());
        verify(postRepository, never()).findByUserInAndStatusOrderByCreatedAtDesc(any(), any());
    }

    @Test
    void testSavePost_Success() throws IOException {
        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[]{1, 2, 3});
        CreatePostDTO dto = CreatePostDTO.builder()
                .title("New Post")
                .description("New Desc")
                .imageFile(file)
                .build();
        when(userService.getLoggedUser()).thenReturn(mockUser);
        postService.savePost(dto, PostStatus.PUBLISHED);

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testUpdatePost_Success_ShouldUpdateFields() throws IOException {
        EditPostDTO dto = EditPostDTO.builder()
                .id(100L)
                .title("Updated Title")
                .description("Updated Desc")
                .build();
        when(postRepository.findById(100L)).thenReturn(Optional.of(mockPost));
        Long authorId = postService.updatePost(dto, PostStatus.PUBLISHED);

        assertEquals(1L, authorId);
        assertEquals("Updated Title", mockPost.getTitle());
        assertEquals("Updated Desc", mockPost.getDescription());
        verify(postRepository, never()).save(any());
    }

    @Test
    void testDeletePost_Success() {
        when(postRepository.findById(100L)).thenReturn(Optional.of(mockPost));
        postService.deletePost(100L);

        verify(postRepository, times(1)).delete(mockPost);
    }

    @Test
    void testFindDraftPostsByUser_ShouldReturnDTOs() {
        when(userService.getLoggedUser()).thenReturn(mockUser);
        when(userService.findById(1L)).thenReturn(mockUser);
        mockPost.setStatus(PostStatus.DRAFT);
        when(postRepository.findByUserAndStatus(mockUser, PostStatus.DRAFT)).thenReturn(List.of(mockPost));
        when(commentService.getCommentsByPostId(100L)).thenReturn(Collections.emptyList());
        List<DisplayPostDTO> result = postService.findDraftPostsByUser(1L);

        assertFalse(result.isEmpty());
        assertEquals(PostStatus.DRAFT.name(), PostStatus.DRAFT.name());
        assertEquals(100L, result.get(0).getId());
    }

    @Test
    void testFindDraftPostsByUser_WhenAdminRequestsOtherUserDrafts_ShouldReturnList() {
        User adminUser = new User();
        adminUser.setId(99L);
        User spyAdmin = spy(adminUser);
        when(spyAdmin.isAdmin()).thenReturn(true);
        when(userService.getLoggedUser()).thenReturn(spyAdmin);
        when(userService.findById(1L)).thenReturn(mockUser);
        mockPost.setStatus(PostStatus.DRAFT);
        when(postRepository.findByUserAndStatus(mockUser, PostStatus.DRAFT)).thenReturn(List.of(mockPost));
        when(commentService.getCommentsByPostId(100L)).thenReturn(Collections.emptyList());
        List<DisplayPostDTO> result = postService.findDraftPostsByUser(1L);

        assertFalse(result.isEmpty());
        verify(postRepository, times(1)).findByUserAndStatus(mockUser, PostStatus.DRAFT);
    }

    @Test
    void testFindDraftPostsByUser_WhenHackerRequestsOtherUserDrafts_ShouldThrowException() {
        when(userService.getLoggedUser()).thenReturn(mockUser);
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            postService.findDraftPostsByUser(2L);
        });

        assertEquals("Drafts not found.", ex.getMessage());

        verify(postRepository, never()).findByUserAndStatus(any(), any());
    }

    @Test
    void testFindFollowedPosts_WithFollowing_ShouldReturnPosts() {
        User followedUser = new User();
        followedUser.setId(2L);
        mockUser.getFollowing().add(followedUser);
        when(userService.getLoggedUser()).thenReturn(mockUser);
        when(postRepository.findByUserInAndStatusOrderByCreatedAtDesc(anyList(), eq(PostStatus.PUBLISHED)))
                .thenReturn(List.of(mockPost));
        when(commentService.getCommentsByPostId(100L)).thenReturn(Collections.emptyList());
        List<DisplayPostDTO> result = postService.findFollowedPosts();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(postRepository).findByUserInAndStatusOrderByCreatedAtDesc(anyList(), eq(PostStatus.PUBLISHED));
    }
}