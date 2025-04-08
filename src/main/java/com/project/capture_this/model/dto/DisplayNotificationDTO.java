package com.project.capture_this.model.dto;

import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayNotificationDTO {
    private Long id;
    private User receiver;
    private User sender;
    private Post post;
    private NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;
}
