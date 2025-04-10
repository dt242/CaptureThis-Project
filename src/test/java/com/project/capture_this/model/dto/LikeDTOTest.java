package com.project.capture_this.model.dto;

import com.project.capture_this.model.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LikeDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        LikeDTO likeDTO = new LikeDTO();

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        LocalDateTime now = LocalDateTime.now();

        likeDTO.setId(100L);
        likeDTO.setUser(mockUser);
        likeDTO.setCreatedAt(now);

        assertEquals(100L, likeDTO.getId());
        assertEquals(mockUser, likeDTO.getUser());
        assertEquals(now, likeDTO.getCreatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User();
        user.setId(2L);
        user.setUsername("anotherUser");

        LocalDateTime time = LocalDateTime.now();

        LikeDTO likeDTO = new LikeDTO(200L, user, time);

        assertEquals(200L, likeDTO.getId());
        assertEquals(user, likeDTO.getUser());
        assertEquals(time, likeDTO.getCreatedAt());
    }

    @Test
    void testBuilder() {
        User user = new User();
        user.setId(3L);
        user.setUsername("builderUser");

        LocalDateTime created = LocalDateTime.now();

        LikeDTO likeDTO = LikeDTO.builder()
                .id(300L)
                .user(user)
                .createdAt(created)
                .build();

        assertEquals(300L, likeDTO.getId());
        assertEquals(user, likeDTO.getUser());
        assertEquals(created, likeDTO.getCreatedAt());
    }
}
