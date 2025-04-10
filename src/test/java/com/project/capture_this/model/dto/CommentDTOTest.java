package com.project.capture_this.model.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentDTOTest {

    @Test
    void testCommentDTOWithNoArgsConstructor() {
        CommentDTO commentDTO = new CommentDTO();

        commentDTO.setId(1L);
        commentDTO.setPostId(100L);
        commentDTO.setUserId(10L);
        commentDTO.setContent("This is a comment.");
        commentDTO.setCreatedAt(LocalDateTime.now());
        commentDTO.setUpdatedAt(LocalDateTime.now());
        commentDTO.setDeleted(false);
        commentDTO.setOwnComment(true);

        assertEquals(1L, commentDTO.getId());
        assertEquals(100L, commentDTO.getPostId());
        assertEquals(10L, commentDTO.getUserId());
        assertEquals("This is a comment.", commentDTO.getContent());
        assertNotNull(commentDTO.getCreatedAt());
        assertNotNull(commentDTO.getUpdatedAt());
        assertFalse(commentDTO.isDeleted());
        assertTrue(commentDTO.isOwnComment());
    }

    @Test
    void testCommentDTOWithBuilder() {
        LocalDateTime now = LocalDateTime.now();
        CommentDTO commentDTO = CommentDTO.builder()
                .id(1L)
                .postId(100L)
                .userId(10L)
                .content("This is a comment.")
                .createdAt(now)
                .updatedAt(now)
                .isDeleted(false)
                .ownComment(true)
                .build();

        assertEquals(1L, commentDTO.getId());
        assertEquals(100L, commentDTO.getPostId());
        assertEquals(10L, commentDTO.getUserId());
        assertEquals("This is a comment.", commentDTO.getContent());
        assertEquals(now, commentDTO.getCreatedAt());
        assertEquals(now, commentDTO.getUpdatedAt());
        assertFalse(commentDTO.isDeleted());
        assertTrue(commentDTO.isOwnComment());
    }

    @Test
    void testCommentDTOEquals() {
        CommentDTO commentDTO1 = CommentDTO.builder()
                .id(1L)
                .postId(100L)
                .userId(10L)
                .content("This is a comment.")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .ownComment(true)
                .build();

        CommentDTO commentDTO2 = CommentDTO.builder()
                .id(1L)
                .postId(100L)
                .userId(10L)
                .content("This is a comment.")
                .createdAt(commentDTO1.getCreatedAt())
                .updatedAt(commentDTO1.getUpdatedAt())
                .isDeleted(false)
                .ownComment(true)
                .build();

        assertEquals(commentDTO1, commentDTO2);
    }

    @Test
    void testCommentDTONotEquals() {
        CommentDTO commentDTO1 = CommentDTO.builder()
                .id(1L)
                .postId(100L)
                .userId(10L)
                .content("This is a comment.")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .ownComment(true)
                .build();

        CommentDTO commentDTO2 = CommentDTO.builder()
                .id(2L)
                .postId(100L)
                .userId(10L)
                .content("This is a comment.")
                .createdAt(commentDTO1.getCreatedAt())
                .updatedAt(commentDTO1.getUpdatedAt())
                .isDeleted(false)
                .ownComment(true)
                .build();

        assertNotEquals(commentDTO1, commentDTO2);
    }
}
