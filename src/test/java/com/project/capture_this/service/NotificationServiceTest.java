package com.project.capture_this.service;

import com.project.capture_this.model.dto.DisplayNotificationDTO;
import com.project.capture_this.model.entity.Notification;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.NotificationType;
import com.project.capture_this.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationService notificationService;

    private User sender;
    private User receiver;
    private Post post;
    private Notification notification;

    @BeforeEach
    public void setUp() {
        sender = new User();
        sender.setId(1L);
        sender.setFirstName("John");
        sender.setLastName("Doe");
        receiver = new User();
        receiver.setId(2L);
        post = new Post();
        post.setId(10L);
        post.setUser(receiver);
        notification = new Notification();
        notification.setId(100L);
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setPost(post);
        notification.setType(NotificationType.LIKE);
        notification.setRead(false);
    }

    @Test
    void testNotifyLike_ShouldSaveNotification() {
        notificationService.notifyLike(sender, post);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testNotifyComment_ShouldSaveNotification() {
        notificationService.notifyComment(sender, post);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testNotifyFollow_ShouldSaveNotification() {
        notificationService.notifyFollow(sender, receiver);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testFindUserUnreadNotifications_ShouldReturnDTOs() {
        when(userService.getLoggedUser()).thenReturn(receiver);
        when(notificationRepository.findByReceiverAndIsReadFalseOrderByCreatedAtDesc(receiver))
                .thenReturn(List.of(notification));
        List<DisplayNotificationDTO> result = notificationService.findUserUnreadNotifications();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getId());
        assertEquals("John", result.get(0).getSenderFirstName());
    }

    @Test
    void testMarkAsRead_Success() {
        when(notificationRepository.findById(100L)).thenReturn(Optional.of(notification));
        notificationService.markAsRead(100L);

        assertTrue(notification.isRead());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testMarkAsRead_NotFound_ShouldThrowException() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> notificationService.markAsRead(999L));
    }

    @Test
    void testMarkAllAsReadForUser_Success() {
        when(userService.getLoggedUser()).thenReturn(receiver);
        when(notificationRepository.findByReceiverAndIsReadFalse(receiver))
                .thenReturn(List.of(notification));
        notificationService.markAllAsReadForUser();

        assertTrue(notification.isRead());
        verify(notificationRepository, never()).saveAll(any());
    }

    @Test
    void testSendEngagementNotifications_ShouldSaveAll() {
        when(userService.findAllUsers()).thenReturn(List.of(receiver, sender));
        notificationService.sendEngagementNotifications();

        verify(notificationRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testGetUnreadNotificationCount() {
        when(notificationRepository.countByReceiverAndIsReadFalse(receiver)).thenReturn(5L);
        long count = notificationService.getUnreadNotificationCount(receiver);

        assertEquals(5L, count);
        verify(notificationRepository).countByReceiverAndIsReadFalse(receiver);
    }
}