package com.project.capture_this.model.entity;

import com.project.capture_this.model.enums.PostStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Post post = new Post();
        User user = new User();
        byte[] image = new byte[]{1, 2, 3};
        String description = "Some description";
        String title = "Post title";

        post.setId(1L);
        post.setUser(user);
        post.setImage(image);
        post.setDescription(description);
        post.setTitle(title);
        post.setStatus(PostStatus.PUBLISHED);

        assertEquals(1L, post.getId());
        assertEquals(user, post.getUser());
        assertArrayEquals(image, post.getImage());
        assertEquals(description, post.getDescription());
        assertEquals(title, post.getTitle());
        assertEquals(PostStatus.PUBLISHED, post.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User();
        byte[] image = new byte[]{5, 6, 7};
        String description = "Cool post!";
        String title = "Title";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        Post post = new Post(
                10L,
                user,
                image,
                description,
                title,
                PostStatus.DRAFT,
                Set.of(),
                createdAt,
                updatedAt
        );

        assertEquals(10L, post.getId());
        assertEquals(user, post.getUser());
        assertArrayEquals(image, post.getImage());
        assertEquals(description, post.getDescription());
        assertEquals(title, post.getTitle());
        assertEquals(PostStatus.DRAFT, post.getStatus());
        assertEquals(createdAt, post.getCreatedAt());
        assertEquals(updatedAt, post.getUpdatedAt());
    }

    @Test
    void testBuilderSetsValuesCorrectly() {
        User user = new User();
        Post post = Post.builder()
                .id(5L)
                .user(user)
                .description("Testing builder")
                .title("Builder title")
                .status(PostStatus.PUBLISHED)
                .build();

        assertEquals(5L, post.getId());
        assertEquals(user, post.getUser());
        assertEquals("Testing builder", post.getDescription());
        assertEquals("Builder title", post.getTitle());
        assertEquals(PostStatus.PUBLISHED, post.getStatus());
    }

    @Test
    void testDefaultPostStatusIsDraft() {
        Post post = new Post();
        assertEquals(PostStatus.DRAFT, post.getStatus());
    }

    @Test
    void testOnCreateSetsCreatedAt() {
        Post post = new Post();
        assertNull(post.getCreatedAt());

        post.onCreate();
        assertNotNull(post.getCreatedAt());
        assertTrue(post.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testOnUpdateSetsUpdatedAt() {
        Post post = new Post();
        assertNull(post.getUpdatedAt());

        post.onUpdate();
        assertNotNull(post.getUpdatedAt());
        assertTrue(post.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testLikesSetIsInitialized() {
        Post post = new Post();
        assertNotNull(post.getLikes());
        assertTrue(post.getLikes().isEmpty());
    }
}
