package com.project.capture_this.controller;

import com.project.capture_this.model.dto.AddProfilePictureDTO;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.service.ProfileService;
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

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @InjectMocks
    private ProfileController profileController;

    @Mock
    private ProfileService profileService;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private User regularUser;

    @BeforeEach
    void setup() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(profileController)
                .setViewResolvers(viewResolver)
                .build();

        regularUser = new User();
        regularUser.setId(1L);
    }

    @Test
    void testViewProfile_Success() throws Exception {
        Map<String, Object> mockData = Map.of("isOwnProfile", true, "profileData", new Object());
        when(profileService.getOwnProfileDetails()).thenReturn(mockData);

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("profilePictureData", "isOwnProfile", "profileData"));
    }

    @Test
    void testViewOtherUserProfile_Success() throws Exception {
        when(userService.getLoggedUser()).thenReturn(regularUser);
        Map<String, Object> mockData = Map.of("isOwnProfile", false, "isFollowing", true);
        when(profileService.getOtherUserProfileDetails(3L)).thenReturn(mockData);

        mockMvc.perform(get("/profile/{userId}", 3L))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("profilePictureData", "isOwnProfile", "isFollowing"));
    }

    @Test
    void testViewOtherUserProfile_WhenUserIdIsSelf_ShouldRedirectToOwnProfile() throws Exception {
        when(userService.getLoggedUser()).thenReturn(regularUser);

        mockMvc.perform(get("/profile/{userId}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));
    }

    @Test
    void testAddProfilePicture_Success() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "profilePicture",
                "test.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        mockMvc.perform(multipart("/profile/add-profile-picture")
                        .file(mockFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("message"));

        verify(profileService, times(1)).updateProfilePicture(null, mockFile);
    }

    @Test
    void testChangeBio_Success() throws Exception {
        mockMvc.perform(post("/profile/change-bio")
                        .param("bio", "Updated Bio"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(profileService, times(1)).updateBio(null, "Updated Bio");
    }

    @Test
    void testChangeFirstName_Success() throws Exception {
        mockMvc.perform(post("/profile/change-first-name")
                        .param("firstName", "UpdatedFirst"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(profileService, times(1)).updateFirstName(null, "UpdatedFirst");
    }

    @Test
    void testChangeLastName_Success() throws Exception {
        mockMvc.perform(post("/profile/change-last-name")
                        .param("lastName", "UpdatedLast"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(profileService, times(1)).updateLastName(null, "UpdatedLast");
    }

    @Test
    void testGetProfilePicture_WhenImageExists() throws Exception {
        byte[] mockImage = "fake_image_bytes".getBytes();
        when(profileService.getProfilePictureBytes(3L)).thenReturn(mockImage);

        mockMvc.perform(get("/profile-picture/{userId}", 3L))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(content().bytes(mockImage));
    }

    @Test
    void testToggleAdmin_Success() throws Exception {
        when(profileService.toggleAdmin(3L)).thenReturn("User is now admin.");

        mockMvc.perform(post("/profile/make-admin/{userId}", 3L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/3"))
                .andExpect(flash().attributeExists("success"))
                .andExpect(flash().attribute("success", "User is now admin."));
    }

    @Test
    void testToggleAdmin_WhenUnauthorized_ShouldReturnError() throws Exception {
        doThrow(new SecurityException("Unauthorized action.")).when(profileService).toggleAdmin(3L);

        mockMvc.perform(post("/profile/make-admin/{userId}", 3L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/3"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error", "Unauthorized action."));
    }
}