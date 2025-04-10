package com.project.capture_this.model.dto;

import com.project.capture_this.model.entity.Role;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.Gender;
import com.project.capture_this.model.enums.UserRoles;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DisplayUserDTOTest {

    @Test
    void testFullConstructorAndGetters() {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1L, UserRoles.USER));

        Set<User> followers = new HashSet<>();
        Set<User> following = new HashSet<>();

        User follower = new User();
        follower.setId(10L);
        follower.setUsername("followerUser");
        followers.add(follower);

        User followingUser = new User();
        followingUser.setId(11L);
        followingUser.setUsername("followingUser");
        following.add(followingUser);

        DisplayUserDTO dto = DisplayUserDTO.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john@example.com")
                .roles(roles)
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .profilePicture(new byte[]{1, 2, 3})
                .bio("Test bio")
                .followers(followers)
                .following(following)
                .createdAt(LocalDateTime.of(2023, 1, 1, 12, 0))
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("johndoe", dto.getUsername());
        assertEquals("john@example.com", dto.getEmail());
        assertEquals(roles, dto.getRoles());
        assertEquals(LocalDate.of(1990, 1, 1), dto.getBirthDate());
        assertEquals(Gender.MALE, dto.getGender());
        assertArrayEquals(new byte[]{1, 2, 3}, dto.getProfilePicture());
        assertEquals("Test bio", dto.getBio());
        assertEquals(followers, dto.getFollowers());
        assertEquals(following, dto.getFollowing());
        assertEquals(LocalDateTime.of(2023, 1, 1, 12, 0), dto.getCreatedAt());
    }

    @Test
    void testCustomConstructor() {
        byte[] picture = new byte[]{5, 6, 7};
        DisplayUserDTO dto = new DisplayUserDTO(2L, "Jane", "Smith", picture);

        assertEquals(2L, dto.getId());
        assertEquals("Jane", dto.getFirstName());
        assertEquals("Smith", dto.getLastName());
        assertArrayEquals(picture, dto.getProfilePicture());

        assertNull(dto.getUsername());
        assertNull(dto.getEmail());
        assertNull(dto.getRoles());
        assertNull(dto.getBirthDate());
        assertNull(dto.getGender());
        assertNull(dto.getBio());
        assertNull(dto.getFollowers());
        assertNull(dto.getFollowing());
        assertNull(dto.getCreatedAt());
    }

    @Test
    void testSetters() {
        DisplayUserDTO dto = new DisplayUserDTO();
        dto.setId(3L);
        dto.setFirstName("Alice");
        dto.setLastName("Wonderland");
        dto.setUsername("alice123");
        dto.setEmail("alice@example.com");
        dto.setBirthDate(LocalDate.of(2000, 5, 15));
        dto.setGender(Gender.FEMALE);
        dto.setProfilePicture(new byte[]{9, 9, 9});
        dto.setBio("Just a test user");
        dto.setCreatedAt(LocalDateTime.now());

        assertEquals(3L, dto.getId());
        assertEquals("Alice", dto.getFirstName());
        assertEquals("Wonderland", dto.getLastName());
        assertEquals("alice123", dto.getUsername());
        assertEquals("alice@example.com", dto.getEmail());
        assertEquals(LocalDate.of(2000, 5, 15), dto.getBirthDate());
        assertEquals(Gender.FEMALE, dto.getGender());
        assertArrayEquals(new byte[]{9, 9, 9}, dto.getProfilePicture());
        assertEquals("Just a test user", dto.getBio());
        assertNotNull(dto.getCreatedAt());
    }
}
