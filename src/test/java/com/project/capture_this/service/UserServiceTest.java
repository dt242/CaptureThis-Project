package com.project.capture_this.service;

import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.model.dto.UserRegisterDTO;
import com.project.capture_this.model.entity.Role;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.Gender;
import com.project.capture_this.model.enums.UserRole;
import com.project.capture_this.repository.UserRepository;
import com.project.capture_this.util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User mockUser;
    private Role mockRole;
    private MockedStatic<SecurityUtil> mockedSecurityUtil;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail("john.doe@example.com");

        mockRole = new Role();
        mockRole.setName(UserRole.USER);

        mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtil.close();
    }

    @Test
    void testGetLoggedUser_Success() {
        mockedSecurityUtil.when(SecurityUtil::getSessionUser).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        User result = userService.getLoggedUser();

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testGetLoggedUser_NotFound_ShouldThrowException() {
        mockedSecurityUtil.when(SecurityUtil::getSessionUser).thenReturn("ghost");
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getLoggedUser());
    }

    @Test
    void testFindById_Success() {
        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(mockUser));
        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindById_NotFound_ShouldThrowException() {
        when(userRepository.findByIdWithRoles(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findById(99L));
    }

    @Test
    void testRegister_Success() {
        UserRegisterDTO dto = new UserRegisterDTO("John", "Doe", "testuser", "john@example.com", Gender.MALE, LocalDate.now(), "pass", "pass");
        when(userRepository.findByUsernameOrEmail("testuser", "john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encoded_pass");
        when(roleService.findByName(UserRole.USER)).thenReturn(mockRole);
        UserService spyService = spy(userService);
        doReturn(new byte[]{1, 2, 3}).when(spyService).getDefaultProfilePicture();
        boolean result = spyService.register(dto);

        assertTrue(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_WhenUserExists_ShouldReturnFalse() {
        UserRegisterDTO dto = new UserRegisterDTO("John", "Doe", "testuser", "john@example.com", Gender.MALE, LocalDate.now(), "pass", "pass");
        when(userRepository.findByUsernameOrEmail("testuser", "john@example.com")).thenReturn(Optional.of(mockUser));
        boolean result = userService.register(dto);

        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testSearchUsers_ShouldReturnDTOs() {
        when(userRepository.findByFirstNameContainingOrLastNameContaining("John", "John"))
                .thenReturn(List.of(mockUser));
        List<DisplayUserDTO> result = userService.searchUsers("John");

        assertFalse(result.isEmpty());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void testSave() {
        userService.save(mockUser);
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testFindAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(mockUser));
        List<User> result = userService.findAllUsers();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
}