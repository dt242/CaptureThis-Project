package com.project.capture_this.model.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayPostDTO {
    private Long id;
    private Long authorId;
    private String authorFirstName;
    private String authorLastName;
    private String description;
    private String title;
    private Set<CommentDTO> comments;
    private Set<LikeDTO> likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
