package com.project.capture_this.service;

import com.project.capture_this.model.entity.User;
import com.project.capture_this.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProfileService profileService;

    private User user;
    private MultipartFile profilePicture;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        profilePicture = mock(MultipartFile.class);
    }

    @Test
    public void testSaveProfilePictureSuccess() throws IOException {
        byte[] pictureBytes = new byte[]{1, 2, 3};
        when(profilePicture.getBytes()).thenReturn(pictureBytes);
        when(userRepository.save(any(User.class))).thenReturn(user);

        profileService.saveProfilePicture(user, profilePicture);

        assertArrayEquals(pictureBytes, user.getProfilePicture());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testSaveProfilePictureIOException() throws IOException {
        when(profilePicture.getBytes()).thenThrow(IOException.class);

        profileService.saveProfilePicture(user, profilePicture);

        verify(userRepository, times(0)).save(user);
    }

    @Test
    public void testUpdateBioSuccess() {
        String newBio = "This is a new bio";
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        profileService.updateBio(1L, newBio);

        assertEquals(newBio, user.getBio());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateBioUserNotFound() {
        String newBio = "This is a new bio";
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> profileService.updateBio(1L, newBio));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void testUpdateFirstNameSuccess() {
        String newFirstName = "NewFirstName";
        when(userService.findById(1L)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        profileService.updateFirstName(1L, newFirstName);

        assertEquals(newFirstName, user.getFirstName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateLastNameSuccess() {
        String newLastName = "NewLastName";
        when(userService.findById(1L)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        profileService.updateLastName(1L, newLastName);

        assertEquals(newLastName, user.getLastName());
        verify(userRepository, times(1)).save(user);
    }
}
