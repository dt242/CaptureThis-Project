package com.project.capture_this.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityUtilTest {

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetSessionUser_WhenUserIsAuthenticated_ShouldReturnUsername() {
        String expectedUsername = "testUser";
        when(authentication.getName()).thenReturn(expectedUsername);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        String result = SecurityUtil.getSessionUser();

        assertEquals(expectedUsername, result);
    }

    @Test
    void testGetSessionUser_WhenUserIsAnonymous_ShouldReturnNull() {
        AnonymousAuthenticationToken anonymousAuth = mock(AnonymousAuthenticationToken.class);
        when(anonymousAuth.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(anonymousAuth);
        SecurityContextHolder.setContext(securityContext);

        String result = SecurityUtil.getSessionUser();

        assertNull(result);
    }

    @Test
    void testGetSessionUser_WhenAuthenticationIsNull_ShouldReturnNull() {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        String result = SecurityUtil.getSessionUser();

        assertNull(result);
    }

    @Test
    void testGetSessionUser_WhenUserIsNotAuthenticated_ShouldReturnNull() {
        when(authentication.isAuthenticated()).thenReturn(false);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String result = SecurityUtil.getSessionUser();

        assertNull(result);
    }
}