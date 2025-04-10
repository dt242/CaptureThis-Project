package com.project.capture_this.model.dto;

import com.project.capture_this.model.enums.Gender;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserRegisterDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setUsername("johndoe");
        dto.setEmail("john@example.com");
        dto.setGender(Gender.MALE);
        dto.setBirthDate(LocalDate.of(1990, 1, 1));
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");

        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("johndoe", dto.getUsername());
        assertEquals("john@example.com", dto.getEmail());
        assertEquals(Gender.MALE, dto.getGender());
        assertEquals(LocalDate.of(1990, 1, 1), dto.getBirthDate());
        assertEquals("password123", dto.getPassword());
        assertEquals("password123", dto.getConfirmPassword());
    }

    @Test
    void testAllArgsConstructor() {
        UserRegisterDTO dto = new UserRegisterDTO(
                "Jane", "Doe", "janedoe", "jane@example.com",
                Gender.FEMALE, LocalDate.of(1995, 5, 5),
                "pass123", "pass123"
        );

        assertEquals("Jane", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("janedoe", dto.getUsername());
        assertEquals("jane@example.com", dto.getEmail());
        assertEquals(Gender.FEMALE, dto.getGender());
        assertEquals(LocalDate.of(1995, 5, 5), dto.getBirthDate());
        assertEquals("pass123", dto.getPassword());
        assertEquals("pass123", dto.getConfirmPassword());
    }

    @Test
    void testBuilder() {
        UserRegisterDTO dto = UserRegisterDTO.builder()
                .firstName("Alex")
                .lastName("Smith")
                .username("alexsmith")
                .email("alex@example.com")
                .gender(Gender.OTHER)
                .birthDate(LocalDate.of(2000, 2, 2))
                .password("alexpass")
                .confirmPassword("alexpass")
                .build();

        assertEquals("Alex", dto.getFirstName());
        assertEquals("Smith", dto.getLastName());
        assertEquals("alexsmith", dto.getUsername());
        assertEquals("alex@example.com", dto.getEmail());
        assertEquals(Gender.OTHER, dto.getGender());
        assertEquals(LocalDate.of(2000, 2, 2), dto.getBirthDate());
        assertEquals("alexpass", dto.getPassword());
        assertEquals("alexpass", dto.getConfirmPassword());
    }
}
