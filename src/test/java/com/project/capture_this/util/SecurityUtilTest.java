package com.project.capture_this.util;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityUtilTest {

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Test
    void testGetSessionUser_whenUserIsAuthenticated() {
        String expectedUsername = "testUser";
        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(expectedUsername);

        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String result = SecurityUtil.getSessionUser();

        assertEquals(expectedUsername, result);
    }

    @Test
    void testGetSessionUser_whenUserIsAnonymous() {
        authentication = mock(AnonymousAuthenticationToken.class);

        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String result = SecurityUtil.getSessionUser();

        assertNull(result);
    }
}
