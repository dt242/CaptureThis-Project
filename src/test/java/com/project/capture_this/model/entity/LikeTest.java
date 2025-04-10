package com.project.capture_this.model.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LikeTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Like like = new Like();
        Post post = new Post();
        User user = new User();

        like.setId(1L);
        like.setPost(post);
        like.setUser(user);
        LocalDateTime now = LocalDateTime.now();
        like.setCreatedAt(now);

        assertEquals(1L, like.getId());
        assertEquals(post, like.getPost());
        assertEquals(user, like.getUser());
        assertEquals(now, like.getCreatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        Post post = new Post();
        User user = new User();
        LocalDateTime createdAt = LocalDateTime.now();

        Like like = new Like(2L, post, user, createdAt);

        assertEquals(2L, like.getId());
        assertEquals(post, like.getPost());
        assertEquals(user, like.getUser());
        assertEquals(createdAt, like.getCreatedAt());
    }

    @Test
    void testOnCreateSetsCreatedAt() {
        Like like = new Like();
        assertNull(like.getCreatedAt());

        like.onCreate();

        assertNotNull(like.getCreatedAt());
        assertTrue(like.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}
