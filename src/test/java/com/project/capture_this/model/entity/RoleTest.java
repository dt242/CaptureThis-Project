package com.project.capture_this.model.entity;

import com.project.capture_this.model.enums.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testEqualsAndHashCode() {
        Role role1 = new Role(1L, UserRole.USER);
        Role role2 = new Role(1L, UserRole.USER);
        Role role3 = new Role(2L, UserRole.ADMIN);

        assertEquals(role1, role2);
        assertEquals(role1.hashCode(), role2.hashCode());
        assertNotEquals(role1, role3);
        assertNotEquals(role1.hashCode(), role3.hashCode());
        assertNotEquals(role1, null);
        assertNotEquals(role1, new Object());
    }
}