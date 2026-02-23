package com.project.capture_this.validation;

import com.project.capture_this.model.dto.UserRegisterDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRegisterDTO> {

    @Override
    public boolean isValid(UserRegisterDTO userRegisterDTO, ConstraintValidatorContext context) {
        if (userRegisterDTO.getPassword() == null || userRegisterDTO.getConfirmPassword() == null) {
            return false;
        }

        return userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword());
    }
}
