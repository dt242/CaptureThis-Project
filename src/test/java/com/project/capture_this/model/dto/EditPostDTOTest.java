package com.project.capture_this.model.dto;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class EditPostDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        EditPostDTO dto = new EditPostDTO();
        dto.setId(1L);
        dto.setTitle("Test Title");
        dto.setDescription("Test Description");

        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "fake image content".getBytes());
        dto.setImageFile(file);

        assertEquals(1L, dto.getId());
        assertEquals("Test Title", dto.getTitle());
        assertEquals("Test Description", dto.getDescription());
        assertEquals(file, dto.getImageFile());
        assertTrue(dto.hasImage());
    }

    @Test
    void testAllArgsConstructor() {
        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "content".getBytes());
        EditPostDTO dto = new EditPostDTO(2L, "Title", "Desc", file);

        assertEquals(2L, dto.getId());
        assertEquals("Title", dto.getTitle());
        assertEquals("Desc", dto.getDescription());
        assertEquals(file, dto.getImageFile());
        assertTrue(dto.hasImage());
    }

    @Test
    void testBuilder() {
        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "content".getBytes());

        EditPostDTO dto = EditPostDTO.builder()
                .id(3L)
                .title("Built Title")
                .description("Built Description")
                .imageFile(file)
                .build();

        assertEquals(3L, dto.getId());
        assertEquals("Built Title", dto.getTitle());
        assertEquals("Built Description", dto.getDescription());
        assertEquals(file, dto.getImageFile());
        assertTrue(dto.hasImage());
    }

    @Test
    void testHasImageReturnsFalseWhenImageIsNull() {
        EditPostDTO dto = new EditPostDTO();
        dto.setImageFile(null);
        assertFalse(dto.hasImage());
    }

    @Test
    void testHasImageReturnsFalseWhenImageIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile("image", "", "image/jpeg", new byte[0]);
        EditPostDTO dto = new EditPostDTO();
        dto.setImageFile(emptyFile);
        assertFalse(dto.hasImage());
    }
}
