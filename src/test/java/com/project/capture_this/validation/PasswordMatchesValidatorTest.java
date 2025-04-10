package com.project.capture_this.validation;

import com.project.capture_this.model.dto.UserRegisterDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordMatchesValidatorTest {

    private final Validator validator;

    public PasswordMatchesValidatorTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    public void testPasswordMatchesInvalid() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setPassword("password123");
        dto.setConfirmPassword("password456");

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}
