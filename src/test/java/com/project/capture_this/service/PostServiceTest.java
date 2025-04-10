package com.project.capture_this.service;

import com.project.capture_this.model.dto.CreatePostDTO;
import com.project.capture_this.model.dto.DisplayPostDTO;
import com.project.capture_this.model.dto.EditPostDTO;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.PostStatus;
import com.project.capture_this.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    private List<Post> mockPosts;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setId(1L);

        mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setTitle("Mock Title");
        mockPost.setDescription("Mock Description");
        mockPost.setUser(mockUser);
        mockPost.setStatus(PostStatus.PUBLISHED);

        mockPosts = new ArrayList<>();
        mockPosts.add(mockPost);
    }

    @Test
    void testFindById() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));

        Post result = postService.findById(1L);

        assertNotNull(result);
        assertEquals(mockPost.getId(), result.getId());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> postService.findById(1L));
    }

    @Test
    void testFindAllPosts() {
        when(postRepository.findAll()).thenReturn(mockPosts);
        when(commentService.getCommentsByPostId(1L)).thenReturn(Collections.emptyList());

        List<DisplayPostDTO> result = postService.findAllPosts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findAll();
    }

    @Test
    void testSavePost() throws IOException {
        MultipartFile mockImageFile = mock(MultipartFile.class);
        when(mockImageFile.getBytes()).thenReturn(new byte[]{1, 2, 3});

        CreatePostDTO createPostDTO = new CreatePostDTO("New Post", "This is a new post", mockImageFile);

        when(userService.getLoggedUser()).thenReturn(mockUser);

        postService.savePost(createPostDTO, PostStatus.PUBLISHED);

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testUpdatePost() throws IOException {
        EditPostDTO editPostDTO = new EditPostDTO(1L, "Updated Title", "Updated Description", null);
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));

        postService.updatePost(editPostDTO, PostStatus.PUBLISHED);

        verify(postRepository, times(1)).save(any(Post.class));
        assertEquals("Updated Title", mockPost.getTitle());
        assertEquals("Updated Description", mockPost.getDescription());
    }

    @Test
    void testDeletePost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));

        postService.deletePost(1L);

        verify(postRepository, times(1)).delete(mockPost);
    }

    @Test
    void testMapToDisplayPostDTO() {
        when(commentService.getCommentsByPostId(1L)).thenReturn(Collections.emptyList());

        DisplayPostDTO result = PostService.mapToDisplayPostDTO(mockPost, commentService);

        assertNotNull(result);
        assertEquals(mockPost.getId(), result.getId());
        assertEquals(mockPost.getTitle(), result.getTitle());
        assertEquals(mockPost.getDescription(), result.getDescription());
    }

    @Test
    void testFindPostsByUser() {
        when(postRepository.findByUserAndStatusOrderByCreatedAtDesc(mockUser, PostStatus.PUBLISHED)).thenReturn(mockPosts);

        List<DisplayPostDTO> result = postService.findPostsByUser(mockUser);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findByUserAndStatusOrderByCreatedAtDesc(mockUser, PostStatus.PUBLISHED);
    }

    @Test
    void testFindPublishedPosts() {
        when(userService.getLoggedUser()).thenReturn(mockUser);
        when(postRepository.findByUserAndStatusOrderByCreatedAtDesc(mockUser, PostStatus.PUBLISHED)).thenReturn(mockPosts);

        List<DisplayPostDTO> result = postService.findPublishedPosts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findByUserAndStatusOrderByCreatedAtDesc(mockUser, PostStatus.PUBLISHED);
    }

    @Test
    void testFindDraftPosts() {
        when(userService.getLoggedUser()).thenReturn(mockUser);
        when(postRepository.findByUserAndStatus(mockUser, PostStatus.DRAFT)).thenReturn(mockPosts);

        List<DisplayPostDTO> result = postService.findDraftPosts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findByUserAndStatus(mockUser, PostStatus.DRAFT);
    }
}
