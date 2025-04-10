package com.project.capture_this.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenderTest {

    @Test
    void testEnumValues() {
        assertEquals(3, Gender.values().length);
        assertEquals(Gender.MALE, Gender.valueOf("MALE"));
        assertEquals(Gender.FEMALE, Gender.valueOf("FEMALE"));
        assertEquals(Gender.OTHER, Gender.valueOf("OTHER"));
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, Gender.MALE.ordinal());
        assertEquals(1, Gender.FEMALE.ordinal());
        assertEquals(2, Gender.OTHER.ordinal());
    }

    @Test
    void testEnumToString() {
        assertEquals("MALE", Gender.MALE.toString());
        assertEquals("FEMALE", Gender.FEMALE.toString());
        assertEquals("OTHER", Gender.OTHER.toString());
    }
}
