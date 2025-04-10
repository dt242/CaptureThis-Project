package com.project.capture_this.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.project.capture_this.controller.HomeController;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.dto.DisplayPostDTO;
import com.project.capture_this.service.PostService;
import com.project.capture_this.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

class HomeControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @InjectMocks
    private HomeController homeController;

    private MockMvc mockMvc;

    private List<DisplayPostDTO> mockPosts;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();

        mockPosts = Arrays.asList(
                new DisplayPostDTO(1L, new User(), new byte[0], "Description 1", "Title 1", null, null, LocalDateTime.now(), LocalDateTime.now()),
                new DisplayPostDTO(2L, new User(), new byte[0], "Description 2", "Title 2", null, null, LocalDateTime.now(), LocalDateTime.now())
        );

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("mockedUser");
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void testIndex() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}
