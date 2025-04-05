package com.project.capture_this.model.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostDTO {
    @NotBlank(message = "Title is mandatory")
    @Size(max = 20, message = "Title must be less than 20 characters")
    private String title;

    @Size(max = 300, message = "Description must be less than 300 characters")
    private String description;

    @NotNull(message = "Image is mandatory")
    private MultipartFile imageFile;

    public boolean hasImage() {
        return imageFile != null && !imageFile.isEmpty();
    }
}
