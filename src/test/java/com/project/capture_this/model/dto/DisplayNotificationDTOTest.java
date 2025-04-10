package com.project.capture_this.model.dto;

import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.NotificationType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DisplayNotificationDTOTest {

    @Test
    void testCreateDisplayNotificationDTO() {
        User receiver = new User();
        receiver.setId(1L);
        receiver.setUsername("receiver");

        User sender = new User();
        sender.setId(2L);
        sender.setUsername("sender");

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Sample Post");

        DisplayNotificationDTO notificationDTO = DisplayNotificationDTO.builder()
                .id(1L)
                .receiver(receiver)
                .sender(sender)
                .post(post)
                .type(NotificationType.LIKE)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        assertNotNull(notificationDTO);
        assertEquals(1L, notificationDTO.getId());
        assertEquals(receiver, notificationDTO.getReceiver());
        assertEquals(sender, notificationDTO.getSender());
        assertEquals(post, notificationDTO.getPost());
        assertEquals(NotificationType.LIKE, notificationDTO.getType());
        assertFalse(notificationDTO.isRead());
        assertNotNull(notificationDTO.getCreatedAt());
    }

    @Test
    void testDTOBuilder() {
        User receiver = new User();
        receiver.setId(1L);
        receiver.setUsername("receiver");

        User sender = new User();
        sender.setId(2L);
        sender.setUsername("sender");

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Sample Post");

        DisplayNotificationDTO notificationDTO = DisplayNotificationDTO.builder()
                .id(2L)
                .receiver(receiver)
                .sender(sender)
                .post(post)
                .type(NotificationType.FOLLOW)
                .isRead(true)
                .createdAt(LocalDateTime.of(2025, 4, 9, 10, 0))
                .build();

        assertEquals(2L, notificationDTO.getId());
        assertEquals(receiver, notificationDTO.getReceiver());
        assertEquals(sender, notificationDTO.getSender());
        assertEquals(post, notificationDTO.getPost());
        assertEquals(NotificationType.FOLLOW, notificationDTO.getType());
        assertTrue(notificationDTO.isRead());
        assertEquals(LocalDateTime.of(2025, 4, 9, 10, 0), notificationDTO.getCreatedAt());
    }

    @Test
    void testDTOSettersAndGetters() {
        DisplayNotificationDTO notificationDTO = new DisplayNotificationDTO();

        User receiver = new User();
        receiver.setId(3L);
        receiver.setUsername("receiver3");

        User sender = new User();
        sender.setId(4L);
        sender.setUsername("sender4");

        Post post = new Post();
        post.setId(3L);
        post.setTitle("Another Sample Post");

        notificationDTO.setId(3L);
        notificationDTO.setReceiver(receiver);
        notificationDTO.setSender(sender);
        notificationDTO.setPost(post);
        notificationDTO.setType(NotificationType.COMMENT);
        notificationDTO.setRead(true);
        notificationDTO.setCreatedAt(LocalDateTime.of(2025, 4, 10, 11, 30));

        assertEquals(3L, notificationDTO.getId());
        assertEquals(receiver, notificationDTO.getReceiver());
        assertEquals(sender, notificationDTO.getSender());
        assertEquals(post, notificationDTO.getPost());
        assertEquals(NotificationType.COMMENT, notificationDTO.getType());
        assertTrue(notificationDTO.isRead());
        assertEquals(LocalDateTime.of(2025, 4, 10, 11, 30), notificationDTO.getCreatedAt());
    }
}
