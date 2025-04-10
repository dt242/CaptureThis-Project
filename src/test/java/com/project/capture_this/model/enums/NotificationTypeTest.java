package com.project.capture_this.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTypeTest {

    @Test
    void testEnumValues() {
        assertEquals(4, NotificationType.values().length);
        assertEquals(NotificationType.LIKE, NotificationType.valueOf("LIKE"));
        assertEquals(NotificationType.COMMENT, NotificationType.valueOf("COMMENT"));
        assertEquals(NotificationType.FOLLOW, NotificationType.valueOf("FOLLOW"));
        assertEquals(NotificationType.ENGAGE, NotificationType.valueOf("ENGAGE"));
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, NotificationType.LIKE.ordinal());
        assertEquals(1, NotificationType.COMMENT.ordinal());
        assertEquals(2, NotificationType.FOLLOW.ordinal());
        assertEquals(3, NotificationType.ENGAGE.ordinal());
    }

    @Test
    void testEnumToString() {
        assertEquals("LIKE", NotificationType.LIKE.toString());
        assertEquals("COMMENT", NotificationType.COMMENT.toString());
        assertEquals("FOLLOW", NotificationType.FOLLOW.toString());
        assertEquals("ENGAGE", NotificationType.ENGAGE.toString());
    }
}
