package com.project.capture_this.controller;

import com.project.capture_this.model.dto.CreatePostDTO;
import com.project.capture_this.model.dto.EditPostDTO;
import com.project.capture_this.service.PostService;
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

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @InjectMocks
    private PostController postController;

    @Mock
    private PostService postService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                .setViewResolvers(viewResolver)
                .build();
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

        verify(postService, times(1)).savePost(any(CreatePostDTO.class), eq("post"));
    }

    @Test
    void testViewEditPost() throws Exception {
        EditPostDTO mockDto = EditPostDTO.builder().id(1L).build();
        when(postService.getPostForEditing(1L)).thenReturn(mockDto);
        when(postService.isPostStatusPublished(1L)).thenReturn(true);

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
        when(postService.updatePost(any(EditPostDTO.class), eq("post"))).thenReturn(1L);

        mockMvc.perform(post("/edit-post")
                        .param("action", "post")
                        .flashAttr("editPostData", editPostDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/1"));

        verify(postService, times(1)).updatePost(any(EditPostDTO.class), eq("post"));
    }

    @Test
    void testDeletePost_Success() throws Exception {
        when(postService.deletePost(1L)).thenReturn(1L);

        mockMvc.perform(post("/delete-post/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/1"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(postService, times(1)).deletePost(1L);
    }

    @Test
    void testViewPost() throws Exception {
        Map<String, Object> mockDetails = Map.of(
                "post", new Object(),
                "comments", new Object(),
                "isLiked", true
        );
        when(postService.getFullPostDetails(1L)).thenReturn(mockDetails);

        mockMvc.perform(get("/post/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("display-post"))
                .andExpect(model().attributeExists("post", "comments", "isLiked"));

        verify(postService, times(1)).getFullPostDetails(1L);
    }
}