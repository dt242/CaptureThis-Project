package com.project.capture_this.model.dto;

import com.project.capture_this.model.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DisplayPostDTOTest {

    @Test
    void testCreateDisplayPostDTO() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");

        CommentDTO comment1 = new CommentDTO(1L, 1L, 1L, "Great post!", LocalDateTime.now(), LocalDateTime.now(), false, false);
        CommentDTO comment2 = new CommentDTO(2L, 1L, 1L, "Nice photo!", LocalDateTime.now(), LocalDateTime.now(), false, false);
        Set<CommentDTO> comments = new HashSet<>();
        comments.add(comment1);
        comments.add(comment2);

        LikeDTO like1 = new LikeDTO(1L, user, LocalDateTime.now());
        Set<LikeDTO> likes = new HashSet<>();
        likes.add(like1);

        DisplayPostDTO displayPostDTO = DisplayPostDTO.builder()
                .id(1L)
                .user(user)
                .image(new byte[]{1, 2, 3})
                .description("This is a test post")
                .title("Test Post")
                .comments(comments)
                .likes(likes)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        assertNotNull(displayPostDTO);
        assertEquals(1L, displayPostDTO.getId());
        assertEquals(user, displayPostDTO.getUser());
        assertArrayEquals(new byte[]{1, 2, 3}, displayPostDTO.getImage());
        assertEquals("This is a test post", displayPostDTO.getDescription());
        assertEquals("Test Post", displayPostDTO.getTitle());
        assertEquals(2, displayPostDTO.getComments().size());
        assertTrue(displayPostDTO.getComments().contains(comment1));
        assertTrue(displayPostDTO.getComments().contains(comment2));
        assertEquals(1, displayPostDTO.getLikes().size());
        assertTrue(displayPostDTO.getLikes().contains(like1));
        assertNotNull(displayPostDTO.getCreatedAt());
        assertNotNull(displayPostDTO.getUpdatedAt());
    }

    @Test
    void testDTOBuilder() {
        User user = new User();
        user.setId(2L);
        user.setUsername("user2");

        Set<CommentDTO> comments = new HashSet<>();
        comments.add(new CommentDTO(1L, 1L, 1L, "First comment", LocalDateTime.now(), LocalDateTime.now(), false, false));
        Set<LikeDTO> likes = new HashSet<>();
        likes.add(new LikeDTO(1L, user, LocalDateTime.now()));

        DisplayPostDTO displayPostDTO = DisplayPostDTO.builder()
                .id(2L)
                .user(user)
                .image(new byte[]{4, 5, 6})
                .description("Another test post")
                .title("Another Post")
                .comments(comments)
                .likes(likes)
                .createdAt(LocalDateTime.of(2025, 4, 9, 14, 30))
                .updatedAt(LocalDateTime.of(2025, 4, 9, 14, 45))
                .build();

        assertEquals(2L, displayPostDTO.getId());
        assertEquals(user, displayPostDTO.getUser());
        assertArrayEquals(new byte[]{4, 5, 6}, displayPostDTO.getImage());
        assertEquals("Another test post", displayPostDTO.getDescription());
        assertEquals("Another Post", displayPostDTO.getTitle());
        assertEquals(1, displayPostDTO.getComments().size());
        assertEquals(1, displayPostDTO.getLikes().size());
        assertEquals(LocalDateTime.of(2025, 4, 9, 14, 30), displayPostDTO.getCreatedAt());
        assertEquals(LocalDateTime.of(2025, 4, 9, 14, 45), displayPostDTO.getUpdatedAt());
    }

    @Test
    void testDTOSettersAndGetters() {
        DisplayPostDTO displayPostDTO = new DisplayPostDTO();

        User user = new User();
        user.setId(3L);
        user.setUsername("user3");

        Set<CommentDTO> comments = new HashSet<>();
        comments.add(new CommentDTO(1L, 1L, 1L, "Comment on post", LocalDateTime.now(), LocalDateTime.now(), false, false));

        Set<LikeDTO> likes = new HashSet<>();
        likes.add(new LikeDTO(1L, user, LocalDateTime.now()));

        displayPostDTO.setId(3L);
        displayPostDTO.setUser(user);
        displayPostDTO.setImage(new byte[]{7, 8, 9});
        displayPostDTO.setDescription("Post description");
        displayPostDTO.setTitle("Post title");
        displayPostDTO.setComments(comments);
        displayPostDTO.setLikes(likes);
        displayPostDTO.setCreatedAt(LocalDateTime.of(2025, 4, 10, 15, 0));
        displayPostDTO.setUpdatedAt(LocalDateTime.of(2025, 4, 10, 15, 15));

        assertEquals(3L, displayPostDTO.getId());
        assertEquals(user, displayPostDTO.getUser());
        assertArrayEquals(new byte[]{7, 8, 9}, displayPostDTO.getImage());
        assertEquals("Post description", displayPostDTO.getDescription());
        assertEquals("Post title", displayPostDTO.getTitle());
        assertEquals(1, displayPostDTO.getComments().size());
        assertEquals(1, displayPostDTO.getLikes().size());
        assertEquals(LocalDateTime.of(2025, 4, 10, 15, 0), displayPostDTO.getCreatedAt());
        assertEquals(LocalDateTime.of(2025, 4, 10, 15, 15), displayPostDTO.getUpdatedAt());
    }
}
