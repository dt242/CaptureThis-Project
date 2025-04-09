package com.project.capture_this.service;

import com.project.capture_this.model.dto.DisplayNotificationDTO;
import com.project.capture_this.model.entity.Notification;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.NotificationType;
import com.project.capture_this.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    public void notifyLike(User likedByUser, Post post) {
        Notification notification = new Notification();
        notification.setSender(likedByUser);
        notification.setReceiver(post.getUser());
        notification.setPost(post);
        notification.setType(NotificationType.LIKE);

        notificationRepository.save(notification);
    }

    public void notifyComment(User commentedByUser, Post post) {
        Notification notification = new Notification();
        notification.setSender(commentedByUser);
        notification.setReceiver(post.getUser());
        notification.setPost(post);
        notification.setType(NotificationType.COMMENT);

        notificationRepository.save(notification);
    }

    public void notifyFollow(User followedByUser, User followedUser) {
        Notification notification = new Notification();
        notification.setSender(followedByUser);
        notification.setReceiver(followedUser);
        notification.setType(NotificationType.FOLLOW);

        notificationRepository.save(notification);
    }

    public List<DisplayNotificationDTO> findUserNotifications() {
        User loggedUser = userService.getLoggedUser();

        List<Notification> notifications = notificationRepository.findByReceiverOrderByCreatedAtDesc(loggedUser);
        return notifications.stream()
                .map(NotificationService::mapToDisplayNotificationDTO)
                .collect(Collectors.toList());
    }

    public List<DisplayNotificationDTO> findUserUnreadNotifications() {
        User loggedUser = userService.getLoggedUser();

        List<Notification> notifications = notificationRepository.findByReceiverAndIsReadFalseOrderByCreatedAtDesc(loggedUser);
        return notifications.stream()
                .map(NotificationService::mapToDisplayNotificationDTO)
                .collect(Collectors.toList());
    }
    public long getUnreadNotificationCount(User user) {
        return notificationRepository.countByReceiverAndIsReadFalse(user);
    }

    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsReadForUser(User user) {
        List<Notification> notifications = notificationRepository.findByReceiverAndIsReadFalse(user);
        for (Notification notification : notifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(notifications);
    }

    @Scheduled(cron = "0 0 10 * * *")
    public void sendEngagementNotifications() {
        List<User> allUsers = userService.findAllUsers();

        for (User user : allUsers) {
            Notification notification = new Notification();
            notification.setReceiver(user);
            notification.setType(NotificationType.ENGAGE);
            notificationRepository.save(notification);
        }
    }


    public static DisplayNotificationDTO mapToDisplayNotificationDTO(Notification notification) {
        return DisplayNotificationDTO.builder()
                .id(notification.getId())
                .receiver(notification.getReceiver())
                .sender(notification.getSender())
                .post(notification.getPost())
                .type(notification.getType())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}

