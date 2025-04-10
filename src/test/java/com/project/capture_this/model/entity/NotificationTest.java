package com.project.capture_this.model.entity;

import com.project.capture_this.model.enums.NotificationType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Notification notification = new Notification();
        User receiver = new User();
        User sender = new User();
        Post post = new Post();
        NotificationType type = NotificationType.LIKE;
        LocalDateTime time = LocalDateTime.now();

        notification.setId(1L);
        notification.setReceiver(receiver);
        notification.setSender(sender);
        notification.setPost(post);
        notification.setType(type);
        notification.setRead(true);
        notification.setCreatedAt(time);

        assertEquals(1L, notification.getId());
        assertEquals(receiver, notification.getReceiver());
        assertEquals(sender, notification.getSender());
        assertEquals(post, notification.getPost());
        assertEquals(type, notification.getType());
        assertTrue(notification.isRead());
        assertEquals(time, notification.getCreatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        User receiver = new User();
        User sender = new User();
        Post post = new Post();
        LocalDateTime createdAt = LocalDateTime.now();

        Notification notification = new Notification(
                2L,
                receiver,
                sender,
                post,
                NotificationType.FOLLOW,
                false,
                createdAt
        );

        assertEquals(2L, notification.getId());
        assertEquals(receiver, notification.getReceiver());
        assertEquals(sender, notification.getSender());
        assertEquals(post, notification.getPost());
        assertEquals(NotificationType.FOLLOW, notification.getType());
        assertFalse(notification.isRead());
        assertEquals(createdAt, notification.getCreatedAt());
    }

    @Test
    void testOnCreateSetsCreatedAt() {
        Notification notification = new Notification();
        assertNull(notification.getCreatedAt());

        notification.onCreate();

        assertNotNull(notification.getCreatedAt());
        assertTrue(notification.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testDefaultIsReadValue() {
        Notification notification = new Notification();
        assertFalse(notification.isRead(), "isRead should be false by default");
    }
}
