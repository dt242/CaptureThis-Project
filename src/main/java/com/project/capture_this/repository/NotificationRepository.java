package com.project.capture_this.repository;

import com.project.capture_this.model.entity.Notification;
import com.project.capture_this.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverOrderByCreatedAtDesc(User loggedUser);

    long countByReceiverAndIsReadFalse(User user);

    List<Notification> findByReceiverAndIsReadFalse(User user);

    List<Notification> findByReceiverAndIsReadFalseOrderByCreatedAtDesc(User loggedUser);
}
