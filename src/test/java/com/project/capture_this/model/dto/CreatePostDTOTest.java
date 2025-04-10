package com.project.capture_this.model.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreatePostDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidCreatePostDTO() {
        CreatePostDTO postDTO = CreatePostDTO.builder()
                .title("Valid Title")
                .description("This is a valid description for the post.")
                .imageFile(new MockMultipartFile("image", "filename.jpg", "image/jpeg", new byte[10]))
                .build();

        Set<ConstraintViolation<CreatePostDTO>> violations = validator.validate(postDTO);

        assertTrue(violations.isEmpty(), "There should be no validation errors.");
    }

    @Test
    void testInvalidTitle() {
        CreatePostDTO postDTO = CreatePostDTO.builder()
                .title("")
                .description("This is a valid description for the post.")
                .imageFile(new MockMultipartFile("image", "filename.jpg", "image/jpeg", new byte[10]))
                .build();

        Set<ConstraintViolation<CreatePostDTO>> violations = validator.validate(postDTO);

        assertFalse(violations.isEmpty(), "There should be validation errors.");
        assertEquals(1, violations.size());
        assertEquals("Title is mandatory", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidDescription() {
        CreatePostDTO postDTO = CreatePostDTO.builder()
                .title("Valid Title")
                .description("A".repeat(501))
                .imageFile(new MockMultipartFile("image", "filename.jpg", "image/jpeg", new byte[10]))
                .build();

        Set<ConstraintViolation<CreatePostDTO>> violations = validator.validate(postDTO);

        assertFalse(violations.isEmpty(), "There should be validation errors.");
        assertEquals(1, violations.size());
        assertEquals("Description must be less than 500 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidImageFile() {
        CreatePostDTO postDTO = CreatePostDTO.builder()
                .title("Valid Title")
                .description("This is a valid description for the post.")
                .imageFile(null)
                .build();

        Set<ConstraintViolation<CreatePostDTO>> violations = validator.validate(postDTO);

        assertFalse(violations.isEmpty(), "There should be validation errors.");
        assertEquals(1, violations.size());
        assertEquals("Image is mandatory", violations.iterator().next().getMessage());
    }
}
