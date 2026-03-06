package com.project.capture_this.model.entity;

import com.project.capture_this.model.enums.UserRole;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testIsAdmin_WhenUserHasAdminRole_ShouldReturnTrue() {
        Role adminRole = new Role(1L, UserRole.ADMIN);
        User user = new User();
        user.setRoles(Set.of(adminRole));

        assertTrue(user.isAdmin());
    }

    @Test
    void testIsAdmin_WhenUserDoesNotHaveAdminRole_ShouldReturnFalse() {
        Role userRole = new Role(2L, UserRole.USER);
        User user = new User();
        user.setRoles(Set.of(userRole));

        assertFalse(user.isAdmin());
    }

    @Test
    void testIsAdmin_WhenUserHasNoRoles_ShouldReturnFalse() {
        User user = new User();
        assertFalse(user.isAdmin());
    }
}