package com.project.capture_this.model.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostDTO {
    @NotBlank(message = "Title is mandatory")
    @Size(max = 20, message = "Title must be less than 20 characters")
    private String title;

    private String description;

    @Column(nullable = false)
    @NotBlank(message = "URL is mandatory")
    private String url;
}
