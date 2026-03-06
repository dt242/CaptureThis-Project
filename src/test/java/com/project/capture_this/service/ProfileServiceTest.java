package com.project.capture_this.service;

import com.project.capture_this.model.entity.User;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileService profileService;

    private User mockUser;

    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("Daniel");
        mockUser.setLastName("Boss");
        mockUser.setBio("Old bio");
    }

    @Test
    void testSaveProfilePicture_Success() throws IOException {
        byte[] bytes = new byte[]{1, 2, 3};
        MultipartFile file = new MockMultipartFile("pic", "test.jpg", "image/jpeg", bytes);
        profileService.saveProfilePicture(mockUser, file);

        assertArrayEquals(bytes, mockUser.getProfilePicture());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testSaveProfilePicture_WhenFileIsEmpty_ShouldThrowException() {
        MultipartFile emptyFile = new MockMultipartFile("pic", new byte[0]);

        assertThrows(IllegalArgumentException.class, () ->
                profileService.saveProfilePicture(mockUser, emptyFile)
        );
    }

    @Test
    void testSaveProfilePicture_WhenIOException_ShouldThrowUncheckedIOException() throws IOException {
        MultipartFile spyFile = spy(new MockMultipartFile("pic", "test.jpg", "image/jpeg", new byte[]{1}));
        when(spyFile.getBytes()).thenThrow(new IOException("Disk error"));

        assertThrows(UncheckedIOException.class, () ->
                profileService.saveProfilePicture(mockUser, spyFile)
        );
    }

    @Test
    void testUpdateBio_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        profileService.updateBio(1L, "New amazing bio");

        assertEquals("New amazing bio", mockUser.getBio());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateBio_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                profileService.updateBio(1L, "Bio")
        );

        assertTrue(ex.getMessage().contains("ID: 1"));
    }

    @Test
    void testUpdateFirstName_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        profileService.updateFirstName(1L, "NewName");

        assertEquals("NewName", mockUser.getFirstName());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateLastName_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        profileService.updateLastName(1L, "NewLastName");

        assertEquals("NewLastName", mockUser.getLastName());
        verify(userRepository, never()).save(any());
    }
}