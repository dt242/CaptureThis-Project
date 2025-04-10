package com.project.capture_this.controller;

import com.project.capture_this.model.dto.CommentDTO;
import com.project.capture_this.model.dto.CreatePostDTO;
import com.project.capture_this.model.dto.DisplayPostDTO;
import com.project.capture_this.model.dto.EditPostDTO;
import com.project.capture_this.model.entity.Like;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.PostStatus;
import com.project.capture_this.service.CommentService;
import com.project.capture_this.service.LikeService;
import com.project.capture_this.service.PostService;
import com.project.capture_this.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PostControllerTest {

    @InjectMocks
    private PostController postController;

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @Mock
    private UserService userService;

    @Mock
    private LikeService likeService;

    private MockMvc mockMvc;

    private Post mockPost;
    private User mockUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();

        mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(userService.getLoggedUser()).thenReturn(mockUser);

        mockPost = mock(Post.class);
        when(mockPost.getId()).thenReturn(1L);
        when(mockPost.getStatus()).thenReturn(PostStatus.DRAFT);
        when(mockPost.getUser()).thenReturn(mockUser);

        when(postService.findById(1L)).thenReturn(mockPost);
        when(commentService.getCommentsByPostId(1L)).thenReturn(Arrays.asList(new CommentDTO()));
        when(likeService.getLikesByPostIdSortedDesc(1L)).thenReturn(Arrays.asList(new Like()));
    }

    @Test
    void testDoCreatePost() throws Exception {
        CreatePostDTO createPostDTO = new CreatePostDTO();
        createPostDTO.setTitle("Test Post");
        createPostDTO.setDescription("Description");
        createPostDTO.setImageFile(mock(org.springframework.web.multipart.MultipartFile.class));

        mockMvc.perform(post("/create-post")
                        .param("action", "post")
                        .flashAttr("createPostData", createPostDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(postService, times(1)).savePost(any(CreatePostDTO.class), eq(PostStatus.PUBLISHED));
    }

    @Test
    void testViewEditPost() throws Exception {
        when(postService.findById(1L)).thenReturn(mockPost);
        mockMvc.perform(get("/edit-post/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-post"))
                .andExpect(model().attributeExists("editPostData"));
    }

    @Test
    void testDoEditPost() throws Exception {
        EditPostDTO editPostDTO = EditPostDTO.builder()
                .id(1L)
                .title("Updated Post")
                .description("Updated Description")
                .build();

        mockMvc.perform(post("/edit-post")
                        .param("action", "post")
                        .flashAttr("editPostData", editPostDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/" + mockPost.getUser().getId()));

        verify(postService, times(1)).updatePost(any(EditPostDTO.class), eq(PostStatus.PUBLISHED));
    }

    @Test
    void testDeletePost() throws Exception {
        when(postService.findById(1L)).thenReturn(mockPost);

        mockMvc.perform(get("/delete-post/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/" + mockPost.getUser().getId()));

        verify(postService, times(1)).deletePost(1L);
    }
}
