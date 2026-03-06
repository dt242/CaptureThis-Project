package com.project.capture_this.validation;

import com.project.capture_this.model.dto.UserRegisterDTO;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PasswordMatchesValidatorTest {

    private PasswordMatchesValidator validator;
    private ConstraintValidatorContext mockContext;

    @BeforeEach
    void setUp() {
        validator = new PasswordMatchesValidator();
        mockContext = mock(ConstraintValidatorContext.class);
    }

    @Test
    void isValid_WhenPasswordsMatch_ShouldReturnTrue() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setPassword("Secret123!");
        dto.setConfirmPassword("Secret123!");

        assertTrue(validator.isValid(dto, mockContext));
    }

    @Test
    void isValid_WhenPasswordsDoNotMatch_ShouldReturnFalse() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setPassword("Secret123!");
        dto.setConfirmPassword("WrongPassword!");

        assertFalse(validator.isValid(dto, mockContext));
    }

    @Test
    void isValid_WhenPasswordIsNull_ShouldReturnFalse() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setPassword(null);
        dto.setConfirmPassword("Secret123!");

        assertFalse(validator.isValid(dto, mockContext));
    }

    @Test
    void isValid_WhenConfirmPasswordIsNull_ShouldReturnFalse() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setPassword("Secret123!");
        dto.setConfirmPassword(null);

        assertFalse(validator.isValid(dto, mockContext));
    }

    @Test
    void isValid_WhenBothAreNull_ShouldReturnFalse() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setPassword(null);
        dto.setConfirmPassword(null);

        assertFalse(validator.isValid(dto, mockContext));
    }
}