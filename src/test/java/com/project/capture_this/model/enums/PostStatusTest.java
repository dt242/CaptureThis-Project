package com.project.capture_this.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostStatusTest {

    @Test
    void testEnumValues() {
        assertEquals(2, PostStatus.values().length);
        assertEquals(PostStatus.DRAFT, PostStatus.valueOf("DRAFT"));
        assertEquals(PostStatus.PUBLISHED, PostStatus.valueOf("PUBLISHED"));
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, PostStatus.DRAFT.ordinal());
        assertEquals(1, PostStatus.PUBLISHED.ordinal());
    }

    @Test
    void testEnumToString() {
        assertEquals("DRAFT", PostStatus.DRAFT.toString());
        assertEquals("PUBLISHED", PostStatus.PUBLISHED.toString());
    }
}
