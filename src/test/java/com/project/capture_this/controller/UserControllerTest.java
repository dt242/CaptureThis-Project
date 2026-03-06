package com.project.capture_this.controller;

import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void testViewRegister_ShouldReturnRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerData"));
    }
    @Test
    void testViewLogin_ShouldReturnLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginData"));
    }

    @Test
    void testViewLoginError_ShouldReturnLoginPageWithError() throws Exception {
        mockMvc.perform(get("/login-error"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginError"))
                .andExpect(model().attribute("loginError", true))
                .andExpect(model().attributeExists("loginData"));
    }

    @Test
    void testSearchUsers_WithValidQuery_ShouldReturnResults() throws Exception {
        DisplayUserDTO foundUser = DisplayUserDTO.builder().username("ivan").build();
        when(userService.searchUsers("ivan")).thenReturn(List.of(foundUser));

        mockMvc.perform(get("/search").param("query", "ivan"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("query", "results"))
                .andExpect(model().attribute("results", org.hamcrest.Matchers.hasSize(1)));

        verify(userService, times(1)).searchUsers("ivan");
    }

    @Test
    void testSearchUsers_WithEmptyQuery_ShouldNotCallDatabase() throws Exception {
        mockMvc.perform(get("/search").param("query", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attribute("results", org.hamcrest.Matchers.empty()));

        verify(userService, never()).searchUsers(anyString());
    }
}