package com.project.capture_this.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditPostDTO {
    private Long id;

    @NotBlank(message = "Title is mandatory")
    @Size(max = 20, message = "Title must be less than 20 characters")
    private String title;

    @Size(max = 300, message = "Description must be less than 300 characters")
    private String description;

    private MultipartFile imageFile;

    public boolean hasImage() {
        return imageFile != null && !imageFile.isEmpty();
    }
}
