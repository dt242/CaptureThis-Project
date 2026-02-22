package com.project.capture_this.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeDTO {
    private Long id;
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private LocalDateTime createdAt;
}
