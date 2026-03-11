package com.project.capture_this.service;

import com.project.capture_this.model.entity.Role;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.UserRole;
import com.project.capture_this.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @Mock
    private FollowService followService;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private ProfileService profileService;

    private User loggedUser;
    private User targetUser;
    private User adminUser;

    @BeforeEach
    public void setUp() {
        loggedUser = new User();
        loggedUser.setId(1L);
        loggedUser.setUsername("dani_regular");
        loggedUser.setRoles(new HashSet<>());
        targetUser = new User();
        targetUser.setId(2L);
        targetUser.setUsername("target_user");
        targetUser.setRoles(new HashSet<>());
        adminUser = new User();
        adminUser.setId(3L);
        adminUser.setUsername("dani_admin");
        Role adminRole = new Role();
        adminRole.setName(UserRole.ADMIN);
        adminUser.setRoles(new HashSet<>(Set.of(adminRole)));
    }

    @Test
    void testUpdateProfilePicture_Success() throws IOException {
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        byte[] bytes = new byte[]{1, 2, 3};
        MultipartFile file = new MockMultipartFile("pic", "test.jpg", "image/jpeg", bytes);
        profileService.updateProfilePicture(null, file);

        assertArrayEquals(bytes, loggedUser.getProfilePicture());
        verify(userRepository, times(1)).save(loggedUser);
    }

    @Test
    void testUpdateProfilePicture_WhenFileIsEmpty_ShouldThrowException() {
        MultipartFile emptyFile = new MockMultipartFile("pic", new byte[0]);

        assertThrows(IllegalArgumentException.class, () ->
                profileService.updateProfilePicture(null, emptyFile)
        );
    }

    @Test
    void testUpdateProfilePicture_WhenIOException_ShouldThrowUncheckedIOException() throws IOException {
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        MultipartFile spyFile = spy(new MockMultipartFile("pic", "test.jpg", "image/jpeg", new byte[]{1}));
        when(spyFile.getBytes()).thenThrow(new IOException("Disk error"));

        assertThrows(UncheckedIOException.class, () ->
                profileService.updateProfilePicture(null, spyFile)
        );
    }

    @Test
    void testUpdateBio_AsRegularUser_UpdatingSelf() {
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        profileService.updateBio(null, "New amazing bio");

        assertEquals("New amazing bio", loggedUser.getBio());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateBio_AsAdmin_UpdatingOtherUser() {
        when(userService.getLoggedUser()).thenReturn(adminUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        profileService.updateBio(2L, "Admin edited bio");

        assertEquals("Admin edited bio", targetUser.getBio());
    }

    @Test
    void testUpdateFirstName_Success() {
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        profileService.updateFirstName(null, "NewName");

        assertEquals("NewName", loggedUser.getFirstName());
    }

    @Test
    void testUpdateLastName_Success() {
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        profileService.updateLastName(null, "NewLastName");

        assertEquals("NewLastName", loggedUser.getLastName());
    }

    @Test
    void testToggleAdmin_WhenLoggedUserIsRegular_ShouldThrowSecurityException() {
        when(userService.getLoggedUser()).thenReturn(loggedUser);

        assertThrows(SecurityException.class, () -> profileService.toggleAdmin(2L));
    }

    @Test
    void testToggleAdmin_AsAdmin_WhenTargetIsRegular_ShouldMakeAdmin() {
        when(userService.getLoggedUser()).thenReturn(adminUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        Role adminRole = new Role();
        adminRole.setName(UserRole.ADMIN);
        when(roleService.findByName(UserRole.ADMIN)).thenReturn(adminRole);
        String result = profileService.toggleAdmin(2L);

        assertTrue(targetUser.isAdmin());
        assertEquals("target_user is now an admin.", result);
    }

    @Test
    void testToggleAdmin_AsAdmin_WhenTargetIsAdmin_ShouldMakeRegularUser() {
        Role adminRole = new Role();
        adminRole.setName(UserRole.ADMIN);
        targetUser.getRoles().add(adminRole);
        when(userService.getLoggedUser()).thenReturn(adminUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(roleService.findByName(UserRole.ADMIN)).thenReturn(adminRole);
        Role userRole = new Role();
        userRole.setName(UserRole.USER);
        when(roleService.findByName(UserRole.USER)).thenReturn(userRole);

        String result = profileService.toggleAdmin(2L);

        assertFalse(targetUser.isAdmin());
        assertEquals("target_user is now a regular user.", result);
    }
}