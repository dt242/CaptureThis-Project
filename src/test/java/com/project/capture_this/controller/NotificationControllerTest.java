package com.project.capture_this.controller;

import com.project.capture_this.model.dto.DisplayNotificationDTO;
import com.project.capture_this.model.enums.NotificationType;
import com.project.capture_this.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private MockMvc mockMvc;

    private List<DisplayNotificationDTO> mockNotifications;

    @BeforeEach
    void setup() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setViewResolvers(viewResolver)
                .build();

        DisplayNotificationDTO notif1 = DisplayNotificationDTO.builder()
                .id(1L)
                .type(NotificationType.LIKE)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .receiverId(1L)
                .senderId(2L)
                .senderFirstName("John")
                .senderLastName("Doe")
                .postId(100L)
                .build();

        mockNotifications = List.of(notif1);
    }

    @Test
    void testNotificationsPage_ShouldReturnViewWithList() throws Exception {
        when(notificationService.findUserUnreadNotifications()).thenReturn(mockNotifications);

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(view().name("notifications"))
                .andExpect(model().attributeExists("notifications"))
                .andExpect(model().attribute("notifications", org.hamcrest.Matchers.hasSize(1)));

        verify(notificationService, times(1)).findUserUnreadNotifications();
    }

    @Test
    void testMarkNotificationAsRead_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/notifications/{id}/read", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notifications"));

        verify(notificationService, times(1)).markAsRead(1L);
    }

    @Test
    void testMarkAllNotificationsAsRead_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/notifications/read-all"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notifications"));

        verify(notificationService, times(1)).markAllAsReadForUser();
    }
}