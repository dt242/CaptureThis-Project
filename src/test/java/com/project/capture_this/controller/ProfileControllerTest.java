package com.project.capture_this.controller;

import com.project.capture_this.model.dto.AddProfilePictureDTO;
import com.project.capture_this.model.entity.Role;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.UserRole;
import com.project.capture_this.service.*;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
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
    private User regularUser;
    private User adminUser;
    private User targetUser;

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
        regularUser.setUsername("testUser");
        regularUser.setRoles(new HashSet<>());

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("adminUser");
        Role adminRole = new Role();
        adminRole.setName(UserRole.ADMIN);
        adminUser.setRoles(new HashSet<>(Set.of(adminRole)));

        targetUser = new User();
        targetUser.setId(3L);
        targetUser.setUsername("targetUser");
        targetUser.setRoles(new HashSet<>());
    }

    @Test
    void testViewProfile_Success() throws Exception {
        when(userService.getLoggedUser()).thenReturn(regularUser);
        when(postService.findPublishedPosts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("profileData", "posts", "isOwnProfile", "isAdmin"))
                .andExpect(model().attribute("isOwnProfile", true));
    }

    @Test
    void testViewOtherUserProfile_Success() throws Exception {
        when(userService.getLoggedUser()).thenReturn(regularUser);
        when(userService.findById(3L)).thenReturn(targetUser);
        when(followService.isFollowing(1L, 3L)).thenReturn(true);
        when(postService.findPostsByUser(targetUser)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/profile/{userId}", 3L))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("isOwnProfile", false))
                .andExpect(model().attribute("isFollowing", true));
    }

    @Test
    void testAddProfilePicture_AsRegularUser_ShouldRedirectToOwnProfile() throws Exception {
        when(userService.getLoggedUser()).thenReturn(regularUser);
        MockMultipartFile mockFile = new MockMultipartFile("profilePicture", "test.jpg", "image/jpeg", "content".getBytes());
        AddProfilePictureDTO addProfilePictureDTO = new AddProfilePictureDTO();
        addProfilePictureDTO.setProfilePicture(mockFile);

        mockMvc.perform(post("/profile/add-profile-picture")
                        .flashAttr("profilePictureData", addProfilePictureDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(profileService).saveProfilePicture(eq(regularUser), any());
    }

    @Test
    void testAddProfilePicture_AsAdmin_ShouldRedirectToTargetProfile() throws Exception {
        when(userService.getLoggedUser()).thenReturn(adminUser);
        when(userService.findById(3L)).thenReturn(targetUser);

        MockMultipartFile mockFile = new MockMultipartFile("profilePicture", "test.jpg", "image/jpeg", "content".getBytes());
        AddProfilePictureDTO addProfilePictureDTO = new AddProfilePictureDTO();
        addProfilePictureDTO.setProfilePicture(mockFile);

        mockMvc.perform(post("/profile/add-profile-picture")
                        .param("userId", "3")
                        .flashAttr("profilePictureData", addProfilePictureDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/3"));

        verify(profileService).saveProfilePicture(eq(targetUser), any());
    }

    @Test
    void testChangeBio_AsRegularUser() throws Exception {
        when(userService.getLoggedUser()).thenReturn(regularUser);

        mockMvc.perform(post("/profile/change-bio")
                        .param("bio", "Updated Bio"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(profileService).updateBio(1L, "Updated Bio");
    }

    @Test
    void testChangeFirstName_AsRegularUser() throws Exception {
        when(userService.getLoggedUser()).thenReturn(regularUser);

        mockMvc.perform(post("/profile/change-first-name")
                        .param("firstName", "UpdatedFirst"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(profileService).updateFirstName(1L, "UpdatedFirst");
    }

    @Test
    void testChangeLastName_AsRegularUser() throws Exception {
        when(userService.getLoggedUser()).thenReturn(regularUser);

        mockMvc.perform(post("/profile/change-last-name")
                        .param("lastName", "UpdatedLast"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(profileService).updateLastName(1L, "UpdatedLast");
    }

    @Test
    void testGetProfilePicture_WhenImageExists() throws Exception {
        byte[] mockImage = "fake_image_bytes".getBytes();
        targetUser.setProfilePicture(mockImage);
        when(userService.findById(3L)).thenReturn(targetUser);

        mockMvc.perform(get("/profile-picture/{userId}", 3L))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(content().bytes(mockImage));
    }

    @Test
    void testToggleAdmin_AsAdmin_ShouldMakeUserAdmin() throws Exception {
        when(userService.getLoggedUser()).thenReturn(adminUser);
        when(userService.findById(3L)).thenReturn(targetUser);

        Role adminRole = new Role();
        adminRole.setName(UserRole.ADMIN);
        when(roleService.findByName(UserRole.ADMIN)).thenReturn(adminRole);

        mockMvc.perform(post("/profile/make-admin/{userId}", 3L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/3"))
                .andExpect(flash().attributeExists("success"));

        verify(userService).save(targetUser);
    }
}