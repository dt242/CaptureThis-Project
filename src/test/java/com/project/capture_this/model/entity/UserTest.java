package com.project.capture_this.model.entity;

import com.project.capture_this.model.enums.Gender;
import com.project.capture_this.model.enums.UserRoles;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testSettersAndGetters() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setPassword("securePass123");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setGender(Gender.MALE);
        user.setBio("Hello world!");
        user.setActive(true);
        user.setLocked(false);
        user.setFailedLoginAttempts(2);

        assertEquals(1L, user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("johndoe", user.getUsername());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("securePass123", user.getPassword());
        assertEquals(LocalDate.of(1990, 1, 1), user.getBirthDate());
        assertEquals(Gender.MALE, user.getGender());
        assertEquals("Hello world!", user.getBio());
        assertTrue(user.isActive());
        assertFalse(user.isLocked());
        assertEquals(2, user.getFailedLoginAttempts());
    }

    @Test
    void testUserIsAdmin() {
        Role adminRole = new Role(1L, UserRoles.ADMIN);
        User user = new User();
        user.setRoles(Set.of(adminRole));

        assertTrue(user.isAdmin());
    }

    @Test
    void testUserIsNotAdmin() {
        Role userRole = new Role(2L, UserRoles.USER);
        User user = new User();
        user.setRoles(Set.of(userRole));

        assertFalse(user.isAdmin());
    }

    @Test
    void testAddFollowersAndFollowing() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        user1.getFollowers().add(user2);
        user1.getFollowing().add(user2);

        assertEquals(1, user1.getFollowers().size());
        assertEquals(1, user1.getFollowing().size());
        assertTrue(user1.getFollowers().contains(user2));
        assertTrue(user1.getFollowing().contains(user2));
    }

    @Test
    void testLifecycle_onCreate() {
        User user = new User();
        assertNull(user.getCreatedAt());

        user.onCreate();
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void testLifecycle_onUpdate() {
        User user = new User();
        assertNull(user.getUpdatedAt());

        user.onUpdate();
        assertNotNull(user.getUpdatedAt());
    }
}
