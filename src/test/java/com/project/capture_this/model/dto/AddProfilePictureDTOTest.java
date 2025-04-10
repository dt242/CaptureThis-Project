package com.project.capture_this.model.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddProfilePictureDTOTest {

    @Mock
    private MultipartFile mockProfilePicture;

    private AddProfilePictureDTO addProfilePictureDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        addProfilePictureDTO = new AddProfilePictureDTO();
    }

    @Test
    void testSetAndGetProfilePicture() {
        when(mockProfilePicture.getOriginalFilename()).thenReturn("profile_pic.jpg");
        addProfilePictureDTO.setProfilePicture(mockProfilePicture);

        MultipartFile profilePicture = addProfilePictureDTO.getProfilePicture();

        assertNotNull(profilePicture);
        assertEquals("profile_pic.jpg", profilePicture.getOriginalFilename());
    }

    @Test
    void testNullProfilePicture() {
        addProfilePictureDTO.setProfilePicture(null);

        MultipartFile profilePicture = addProfilePictureDTO.getProfilePicture();

        assertNull(profilePicture);
    }
}
