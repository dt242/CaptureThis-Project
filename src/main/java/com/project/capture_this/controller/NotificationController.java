package com.project.capture_this.controller;

import com.project.capture_this.util.SecurityUtil;
import com.project.capture_this.model.dto.DisplayNotificationDTO;
import com.project.capture_this.service.NotificationService;
import com.project.capture_this.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService, UserService userService) {

        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {
        List<DisplayNotificationDTO> allUserUnreadNotifications = notificationService.findUserUnreadNotifications();
        if (SecurityUtil.getSessionUser() != null) {
            model.addAttribute("user", userService.getLoggedUser());
        }
        model.addAttribute("notifications", allUserUnreadNotifications);
        return "notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllAsRead() {
        notificationService.markAllAsReadForUser(userService.getLoggedUser());
        return "redirect:/notifications";
    }
}
