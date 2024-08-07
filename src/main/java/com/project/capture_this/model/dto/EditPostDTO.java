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
    private Long id; // Required for updates

    @NotBlank(message = "Title is mandatory")
    @Size(max = 20, message = "Title must be less than 20 characters")
    private String title;

    @Size(max = 150, message = "Description must be less than 150 characters")
    private String description;

    private MultipartFile imageFile; // Optional for updates

    public boolean hasImage() {
        return imageFile != null && !imageFile.isEmpty();
    }
}
