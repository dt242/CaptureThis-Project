package com.project.capture_this.config;

import com.project.capture_this.model.entity.User;
import com.project.capture_this.service.NotificationService;
import com.project.capture_this.service.UserService;
import com.project.capture_this.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalModelAttributesTest {

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private Model model;

    @InjectMocks
    private GlobalModelAttributes globalModelAttributes;

    @BeforeEach
    void setUp() {
        globalModelAttributes = new GlobalModelAttributes(userService, notificationService);
    }

    @Test
    void testAddGlobalAttributes_whenUserIsLoggedIn() {
        User user = new User();
        user.setId(1L);
        long unreadCount = 5L;

        try (MockedStatic<SecurityUtil> mockSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockSecurityUtil.when(SecurityUtil::getSessionUser).thenReturn("testUser");

            when(userService.getLoggedUser()).thenReturn(user);
            when(notificationService.getUnreadNotificationCount(user)).thenReturn(unreadCount);

            globalModelAttributes.addGlobalAttributes(model);

            verify(model, times(1)).addAttribute("user", user);
            verify(model, times(1)).addAttribute("unreadCount", unreadCount);
        }
    }
}
