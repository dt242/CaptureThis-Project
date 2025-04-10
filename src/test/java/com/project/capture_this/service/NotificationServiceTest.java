package com.project.capture_this.service;

import com.project.capture_this.model.dto.DisplayNotificationDTO;
import com.project.capture_this.model.entity.Notification;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.NotificationType;
import com.project.capture_this.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
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

    @Mock
    private PostService postService;

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
        sender.setUsername("senderUser");

        receiver = new User();
        receiver.setId(2L);
        receiver.setUsername("receiverUser");

        post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setUser(receiver);

        notification = new Notification();
        notification.setId(1L);
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setPost(post);
        notification.setType(NotificationType.LIKE);
    }

    @Test
    public void testNotifyLike() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.notifyLike(sender, post);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    public void testNotifyComment() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.notifyComment(sender, post);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    public void testNotifyFollow() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.notifyFollow(sender, receiver);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    public void testFindUserNotifications() {
        List<Notification> notifications = new ArrayList<>();
        notifications.add(notification);
        when(userService.getLoggedUser()).thenReturn(receiver);
        when(notificationRepository.findByReceiverOrderByCreatedAtDesc(receiver)).thenReturn(notifications);

        List<DisplayNotificationDTO> displayNotifications = notificationService.findUserNotifications();

        assertEquals(1, displayNotifications.size());
        assertEquals(notification.getType(), displayNotifications.get(0).getType());
        verify(notificationRepository, times(1)).findByReceiverOrderByCreatedAtDesc(receiver);
    }

    @Test
    public void testFindUserUnreadNotifications() {
        List<Notification> notifications = new ArrayList<>();
        notifications.add(notification);
        when(userService.getLoggedUser()).thenReturn(receiver);
        when(notificationRepository.findByReceiverAndIsReadFalseOrderByCreatedAtDesc(receiver)).thenReturn(notifications);

        List<DisplayNotificationDTO> displayNotifications = notificationService.findUserUnreadNotifications();

        assertEquals(1, displayNotifications.size());
        assertEquals(notification.getType(), displayNotifications.get(0).getType());
        verify(notificationRepository, times(1)).findByReceiverAndIsReadFalseOrderByCreatedAtDesc(receiver);
    }

    @Test
    public void testMarkAsRead() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.markAsRead(1L);

        assertTrue(notification.isRead());
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    public void testMarkAsReadNotificationNotFound() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> notificationService.markAsRead(1L));

        assertEquals("Notification not found", exception.getMessage());
    }

    @Test
    public void testMarkAllAsReadForUser() {
        List<Notification> notifications = new ArrayList<>();
        notifications.add(notification);
        when(notificationRepository.findByReceiverAndIsReadFalse(receiver)).thenReturn(notifications);
        when(notificationRepository.saveAll(notifications)).thenReturn(notifications);

        notificationService.markAllAsReadForUser(receiver);

        assertTrue(notifications.get(0).isRead());
        verify(notificationRepository, times(1)).saveAll(notifications);
    }

    @Test
    public void testSendEngagementNotifications() {
        List<User> users = new ArrayList<>();
        users.add(receiver);
        when(userService.findAllUsers()).thenReturn(users);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.sendEngagementNotifications();

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
}
