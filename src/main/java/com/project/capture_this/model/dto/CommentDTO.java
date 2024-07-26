package com.project.capture_this.model.dto;

import com.project.capture_this.model.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private User user;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
