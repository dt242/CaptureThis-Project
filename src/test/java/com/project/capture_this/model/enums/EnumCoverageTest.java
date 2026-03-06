package com.project.capture_this.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EnumCoverageTest {

    @Test
    void testEnumGeneratedMethodsForCoverage() {
        for (Gender gender : Gender.values()) {
            assertNotNull(Gender.valueOf(gender.name()));
        }

        for (NotificationType type : NotificationType.values()) {
            assertNotNull(NotificationType.valueOf(type.name()));
        }

        for (PostStatus status : PostStatus.values()) {
            assertNotNull(PostStatus.valueOf(status.name()));
        }

        for (UserRole role : UserRole.values()) {
            assertNotNull(UserRole.valueOf(role.name()));
        }
    }
}