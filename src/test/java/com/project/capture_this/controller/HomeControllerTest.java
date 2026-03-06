package com.project.capture_this.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.project.capture_this.model.dto.DisplayPostDTO;
import com.project.capture_this.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private HomeController homeController;

    private MockMvc mockMvc;

    private List<DisplayPostDTO> mockPosts;

    @BeforeEach
    void setup() {
        org.springframework.web.servlet.view.InternalResourceViewResolver viewResolver =
                new org.springframework.web.servlet.view.InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(homeController)
                .setViewResolvers(viewResolver)
                .build();

        DisplayPostDTO post1 = DisplayPostDTO.builder()
                .id(1L)
                .title("Title 1")
                .description("Description 1")
                .createdAt(LocalDateTime.now())
                .build();

        DisplayPostDTO post2 = DisplayPostDTO.builder()
                .id(2L)
                .title("Title 2")
                .description("Description 2")
                .createdAt(LocalDateTime.now())
                .build();

        mockPosts = List.of(post1, post2);
    }

    @Test
    void testHome_ShouldReturnHomeViewWithPosts() throws Exception {
        when(postService.findFollowedPosts()).thenReturn(mockPosts);
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("posts", org.hamcrest.Matchers.hasSize(2)));
        verify(postService, times(1)).findFollowedPosts();
    }
}