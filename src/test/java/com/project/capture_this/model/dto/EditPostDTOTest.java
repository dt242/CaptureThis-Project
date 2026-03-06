package com.project.capture_this.model.dto;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class EditPostDTOTest {

    @Test
    void testHasImage_WhenFileIsPresent_ShouldReturnTrue() {
        EditPostDTO dto = new EditPostDTO();
        dto.setImageFile(new MockMultipartFile("file", "test.jpg", "image/jpeg", "data".getBytes()));

        assertTrue(dto.hasImage());
    }

    @Test
    void testHasImage_WhenFileIsNull_ShouldReturnFalse() {
        EditPostDTO dto = new EditPostDTO();
        dto.setImageFile(null);

        assertFalse(dto.hasImage());
    }

    @Test
    void testHasImage_WhenFileIsEmpty_ShouldReturnFalse() {
        EditPostDTO dto = new EditPostDTO();
        dto.setImageFile(new MockMultipartFile("file", "", "image/jpeg", new byte[0]));

        assertFalse(dto.hasImage());
    }
}