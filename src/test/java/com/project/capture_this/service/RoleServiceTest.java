package com.project.capture_this.service;

import com.project.capture_this.model.entity.Role;
import com.project.capture_this.model.enums.UserRole;
import com.project.capture_this.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
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

    private Role mockRole;

    @BeforeEach
    public void setUp() {
        mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setName(UserRole.ADMIN);
    }

    @Test
    void testFindByName_WhenRoleExists_ShouldReturnRole() {
        when(roleRepository.findByName(UserRole.ADMIN)).thenReturn(Optional.of(mockRole));
        Role result = roleService.findByName(UserRole.ADMIN);

        assertNotNull(result);
        assertEquals(UserRole.ADMIN, result.getName());
        assertEquals(1L, result.getId());
        verify(roleRepository, times(1)).findByName(UserRole.ADMIN);
    }

    @Test
    void testFindByName_WhenRoleDoesNotExist_ShouldThrowEntityNotFoundException() {
        when(roleRepository.findByName(UserRole.USER)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                roleService.findByName(UserRole.USER)
        );

        assertEquals("Role not found: USER", exception.getMessage());
        verify(roleRepository, times(1)).findByName(UserRole.USER);
    }
}