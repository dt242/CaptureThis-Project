package com.project.capture_this.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRolesTest {

    @Test
    void testEnumValues() {
        assertEquals(2, UserRoles.values().length);
        assertEquals(UserRoles.USER, UserRoles.valueOf("USER"));
        assertEquals(UserRoles.ADMIN, UserRoles.valueOf("ADMIN"));
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, UserRoles.USER.ordinal());
        assertEquals(1, UserRoles.ADMIN.ordinal());
    }

    @Test
    void testEnumToString() {
        assertEquals("USER", UserRoles.USER.toString());
        assertEquals("ADMIN", UserRoles.ADMIN.toString());
    }
}
