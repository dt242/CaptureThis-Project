package com.project.capture_this.service;

import com.project.capture_this.model.entity.Role;
import com.project.capture_this.model.enums.UserRoles;
import com.project.capture_this.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role role;

    @BeforeEach
    public void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName(UserRoles.ADMIN);
    }

    @Test
    public void testFindByNameSuccess() {
        when(roleRepository.findByName(UserRoles.ADMIN)).thenReturn(Optional.of(role));

        Role foundRole = roleService.findByName(UserRoles.ADMIN);

        assertNotNull(foundRole);
        assertEquals(UserRoles.ADMIN, foundRole.getName());
    }

    @Test
    public void testFindByNameRoleNotFound() {
        when(roleRepository.findByName(UserRoles.ADMIN)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> roleService.findByName(UserRoles.ADMIN));

        assertEquals("Role not found: ADMIN", exception.getMessage());
    }
}
