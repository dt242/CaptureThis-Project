package com.project.capture_this.config;

import com.project.capture_this.model.entity.User;
import com.project.capture_this.service.NotificationService;
import com.project.capture_this.service.UserService;
import com.project.capture_this.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public GlobalModelAttributes(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        if (SecurityUtil.getSessionUser() != null) {
            User user = userService.getLoggedUser();
            long unreadCount = notificationService.getUnreadNotificationCount(user);
            model.addAttribute("user", user);
            model.addAttribute("unreadCount", unreadCount);
        }
    }
}
