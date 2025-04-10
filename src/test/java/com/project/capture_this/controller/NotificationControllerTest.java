package com.project.capture_this.controller;

import com.project.capture_this.controller.NotificationController;
import com.project.capture_this.model.dto.DisplayNotificationDTO;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.NotificationType;
import com.project.capture_this.service.NotificationService;
import com.project.capture_this.service.UserService;
import com.project.capture_this.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationController notificationController;

    private MockMvc mockMvc;

    private User mockReceiver;
    private User mockSender;
    private Post mockPost;
    private List<DisplayNotificationDTO> mockNotifications;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();

        mockReceiver = mock(User.class);
        mockSender = mock(User.class);
        mockPost = mock(Post.class);

        when(mockReceiver.getId()).thenReturn(1L);
        when(mockSender.getId()).thenReturn(2L);
        when(mockPost.getId()).thenReturn(1L);

        mockNotifications = Arrays.asList(
                DisplayNotificationDTO.builder()
                        .id(1L)
                        .receiver(mockReceiver)
                        .sender(mockSender)
                        .post(mockPost)
                        .type(NotificationType.LIKE)
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build(),
                DisplayNotificationDTO.builder()
                        .id(2L)
                        .receiver(mockReceiver)
                        .sender(mockSender)
                        .post(mockPost)
                        .type(NotificationType.COMMENT)
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("mockedUser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testMarkNotificationAsRead() throws Exception {
        when(userService.getLoggedUser()).thenReturn(mockReceiver);

        mockMvc.perform(post("/notifications/{id}/read", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notifications"));

        verify(notificationService).markAsRead(1L);
    }

    @Test
    void testMarkAllNotificationsAsRead() throws Exception {
        when(userService.getLoggedUser()).thenReturn(mockReceiver);

        mockMvc.perform(post("/notifications/read-all"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notifications"));

        verify(notificationService).markAllAsReadForUser(any());
    }
}
