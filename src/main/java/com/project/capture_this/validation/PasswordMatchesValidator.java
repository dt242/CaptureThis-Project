package com.project.capture_this.validation;

import com.project.capture_this.model.dto.UserRegisterDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRegisterDTO> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(UserRegisterDTO userRegisterDTO, ConstraintValidatorContext context) {
        boolean valid = userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword());
        if (!valid) {
            System.out.println("Passwords do not match");
        }
        return valid;
    }
}
