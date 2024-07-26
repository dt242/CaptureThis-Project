package com.project.capture_this.service;

import com.project.capture_this.model.dto.CommentDTO;
import com.project.capture_this.model.entity.Comment;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    public static CommentDTO mapToCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .user(comment.getUser())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
