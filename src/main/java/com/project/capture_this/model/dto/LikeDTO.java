package com.project.capture_this.model.dto;

import com.project.capture_this.model.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeDTO {
    private Long id;
    private User user;
    private LocalDateTime createdAt;
}
