package com.project.capture_this.model.entity;

import com.project.capture_this.model.enums.UserRoles;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Role role = new Role();
        role.setId(1L);
        role.setName(UserRoles.ADMIN);

        assertEquals(1L, role.getId());
        assertEquals(UserRoles.ADMIN, role.getName());
    }

    @Test
    void testAllArgsConstructor() {
        Role role = new Role(2L, UserRoles.USER);

        assertEquals(2L, role.getId());
        assertEquals(UserRoles.USER, role.getName());
    }

    @Test
    void testEqualsAndHashCode_SameValues() {
        Role role1 = new Role(1L, UserRoles.ADMIN);
        Role role2 = new Role(1L, UserRoles.ADMIN);

        assertEquals(role1, role2);
        assertEquals(role1.hashCode(), role2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentValues() {
        Role role1 = new Role(1L, UserRoles.ADMIN);
        Role role2 = new Role(2L, UserRoles.USER);

        assertNotEquals(role1, role2);
        assertNotEquals(role1.hashCode(), role2.hashCode());
    }

    @Test
    void testEqualsWithNullAndDifferentClass() {
        Role role = new Role(1L, UserRoles.ADMIN);

        assertNotEquals(null, role);
        assertNotEquals("Some string", role);
    }

    @Test
    void testEnumUniqueness() {
        assertTrue(UserRoles.values().length > 0);
        assertNotNull(UserRoles.ADMIN.name());
    }
}
