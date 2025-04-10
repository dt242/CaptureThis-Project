package com.project.capture_this.model.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserLoginDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        UserLoginDTO dto = new UserLoginDTO();

        dto.setUsername("testUser");
        dto.setPassword("securePass");

        assertEquals("testUser", dto.getUsername());
        assertEquals("securePass", dto.getPassword());
    }

    @Test
    void testAllArgsConstructor() {
        UserLoginDTO dto = new UserLoginDTO("user1", "password123");

        assertEquals("user1", dto.getUsername());
        assertEquals("password123", dto.getPassword());
    }

    @Test
    void testBuilder() {
        UserLoginDTO dto = UserLoginDTO.builder()
                .username("builderUser")
                .password("builderPass")
                .build();

        assertEquals("builderUser", dto.getUsername());
        assertEquals("builderPass", dto.getPassword());
    }
}
