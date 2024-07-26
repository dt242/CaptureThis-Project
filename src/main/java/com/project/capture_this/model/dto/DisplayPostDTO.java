package com.project.capture_this.model.dto;

import com.project.capture_this.model.entity.Comment;
import com.project.capture_this.model.entity.Like;
import com.project.capture_this.model.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisplayPostDTO {
    private Long id;
    private User user;
    private String url;
    private String description;
    private String title;
    private Set<CommentDTO> comments;
    private Set<LikeDTO> likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
