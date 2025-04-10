package com.project.capture_this.controller;

import com.project.capture_this.model.dto.AddProfilePictureDTO;
import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProfileControllerTest {

    @InjectMocks
    private ProfileController profileController;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @Mock
    private FollowService followService;

    @Mock
    private ProfileService profileService;

    @Mock
    private RoleService roleService;

    private MockMvc mockMvc;
    private User mockUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(profileController).build();

        mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getUsername()).thenReturn("testUser");
        when(mockUser.isAdmin()).thenReturn(false);
        when(userService.getLoggedUser()).thenReturn(mockUser);
    }

    @Test
    void testAddProfilePicture() throws Exception {
        AddProfilePictureDTO addProfilePictureDTO = new AddProfilePictureDTO();
        addProfilePictureDTO.setProfilePicture(mock(org.springframework.web.multipart.MultipartFile.class));

        mockMvc.perform(post("/profile/add-profile-picture")
                        .param("userId", "1")
                        .flashAttr("profilePictureData", addProfilePictureDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/1"));

        verify(profileService, times(1)).saveProfilePicture(any(User.class), any());
    }

    @Test
    void testChangeBio() throws Exception {
        String bio = "Updated Bio";

        mockMvc.perform(post("/profile/change-bio")
                        .param("bio", bio)
                        .param("userId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(profileService, times(1)).updateBio(eq(1L), eq(bio));
    }

    @Test
    void testChangeFirstName() throws Exception {
        String firstName = "UpdatedFirstName";

        mockMvc.perform(post("/profile/change-first-name")
                        .param("firstName", firstName)
                        .param("userId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(profileService, times(1)).updateFirstName(eq(1L), eq(firstName));
    }

    @Test
    void testChangeLastName() throws Exception {
        String lastName = "UpdatedLastName";

        mockMvc.perform(post("/profile/change-last-name")
                        .param("lastName", lastName)
                        .param("userId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(profileService, times(1)).updateLastName(eq(1L), eq(lastName));
    }

    @Test
    void testGetProfilePicture() throws Exception {
        byte[] mockImage = new byte[10];
        when(userService.findById(1L)).thenReturn(mockUser);
        when(mockUser.getProfilePicture()).thenReturn(mockImage);

        mockMvc.perform(get("/profile-picture/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(content().bytes(mockImage));
    }
}