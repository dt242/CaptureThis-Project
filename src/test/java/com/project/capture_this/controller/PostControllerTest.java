package com.project.capture_this.controller;

import com.project.capture_this.model.dto.CommentDTO;
import com.project.capture_this.model.dto.CreatePostDTO;
import com.project.capture_this.model.dto.EditPostDTO;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.PostStatus;
import com.project.capture_this.service.CommentService;
import com.project.capture_this.service.LikeService;
import com.project.capture_this.service.PostService;
import com.project.capture_this.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
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
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                .setViewResolvers(viewResolver)
                .build();

        mockUser = new User();
        mockUser.setId(1L);

        mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setStatus(PostStatus.DRAFT);
        mockPost.setUser(mockUser);
    }

    @Test
    void testDoCreatePost_Success() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("imageFile", "test.jpg", "image/jpeg", "test image content".getBytes());
        CreatePostDTO createPostDTO = new CreatePostDTO();
        createPostDTO.setTitle("Test Post");
        createPostDTO.setDescription("Description");
        createPostDTO.setImageFile(mockFile);

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
                .andExpect(model().attributeExists("editPostData"))
                .andExpect(model().attributeExists("isAlreadyPosted"));
    }

    @Test
    void testDoEditPost_Success() throws Exception {
        EditPostDTO editPostDTO = EditPostDTO.builder()
                .id(1L)
                .title("Updated Post")
                .description("Updated Description")
                .build();

        when(postService.updatePost(any(EditPostDTO.class), eq(PostStatus.PUBLISHED))).thenReturn(1L);

        mockMvc.perform(post("/edit-post")
                        .param("action", "post")
                        .flashAttr("editPostData", editPostDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/1"));

        verify(postService, times(1)).updatePost(any(EditPostDTO.class), eq(PostStatus.PUBLISHED));
    }

    @Test
    void testDeletePost_Success() throws Exception {
        when(postService.findById(1L)).thenReturn(mockPost);

        mockMvc.perform(post("/delete-post/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/1"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(postService, times(1)).deletePost(1L);
    }

    @Test
    void testViewPost() throws Exception {
        when(postService.findById(1L)).thenReturn(mockPost);
        when(userService.getLoggedUser()).thenReturn(mockUser);

        CommentDTO mockComment = new CommentDTO();
        mockComment.setUserId(2L);
        mockComment.setCreatedAt(LocalDateTime.now());
        when(commentService.getCommentsByPostId(1L)).thenReturn(List.of(mockComment));
        User commentAuthor = new User();
        commentAuthor.setId(2L);
        when(userService.findById(2L)).thenReturn(commentAuthor);
        when(likeService.getLikesByPostIdSortedDesc(1L)).thenReturn(Collections.emptyList());
        when(likeService.isUserLikedPost(1L, mockUser)).thenReturn(true);

        mockMvc.perform(get("/post/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("display-post"))
                .andExpect(model().attributeExists("post", "comments", "commentAuthors", "likes", "isLiked", "isAdmin"))
                .andExpect(model().attribute("isLiked", true));

        verify(postService).findById(1L);
        verify(commentService).getCommentsByPostId(1L);
    }
}