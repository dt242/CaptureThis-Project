package com.project.capture_this.service;

import com.project.capture_this.model.dto.DisplayNotificationDTO;
import com.project.capture_this.model.entity.Notification;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.NotificationType;
import com.project.capture_this.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public NotificationService(NotificationRepository notificationRepository, UserService userService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

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

    @Transactional
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with ID: " + id));
        notification.setRead(true);
    }

    @Transactional
    public void markAllAsReadForUser() {
        User loggedUser = userService.getLoggedUser();
        List<Notification> notifications = notificationRepository.findByReceiverAndIsReadFalse(loggedUser);
        for (Notification notification : notifications) {
            notification.setRead(true);
        }
    }

    @Scheduled(cron = "0 0 10 * * *")
    public void sendEngagementNotifications() {
        List<User> allUsers = userService.findAllUsers();

        List<Notification> notificationsToSave = new ArrayList<>();

        for (User user : allUsers) {
            Notification notification = new Notification();
            notification.setReceiver(user);
            notification.setType(NotificationType.ENGAGE);
            notificationsToSave.add(notification);
        }

        notificationRepository.saveAll(notificationsToSave);
    }

    public static DisplayNotificationDTO mapToDisplayNotificationDTO(Notification notification) {
        DisplayNotificationDTO.DisplayNotificationDTOBuilder builder = DisplayNotificationDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt());

        if (notification.getReceiver() != null) {
            builder.receiverId(notification.getReceiver().getId());
        }

        if (notification.getSender() != null) {
            builder.senderId(notification.getSender().getId())
                    .senderFirstName(notification.getSender().getFirstName())
                    .senderLastName(notification.getSender().getLastName());
        }

        if (notification.getPost() != null) {
            builder.postId(notification.getPost().getId());
        }

        return builder.build();
    }
}