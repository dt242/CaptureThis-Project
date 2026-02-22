package com.project.capture_this.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    void testEnumValues() {
        assertEquals(2, UserRole.values().length);
        assertEquals(UserRole.USER, UserRole.valueOf("USER"));
        assertEquals(UserRole.ADMIN, UserRole.valueOf("ADMIN"));
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, UserRole.USER.ordinal());
        assertEquals(1, UserRole.ADMIN.ordinal());
    }

    @Test
    void testEnumToString() {
        assertEquals("USER", UserRole.USER.toString());
        assertEquals("ADMIN", UserRole.ADMIN.toString());
    }
}
