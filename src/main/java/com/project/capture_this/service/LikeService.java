package com.project.capture_this.service;

import com.project.capture_this.model.dto.LikeDTO;
import com.project.capture_this.model.entity.Like;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    public static LikeDTO mapToLikeDTO(Like like){
        return LikeDTO.builder()
                .id(like.getId())
                .user(like.getUser())
                .createdAt(like.getCreatedAt())
                .build();
    }
}
