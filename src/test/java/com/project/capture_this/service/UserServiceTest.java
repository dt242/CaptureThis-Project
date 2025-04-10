package com.project.capture_this.service;

import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.model.dto.UserRegisterDTO;
import com.project.capture_this.model.entity.Role;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.Gender;
import com.project.capture_this.model.enums.UserRoles;
import com.project.capture_this.repository.RoleRepository;
import com.project.capture_this.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User mockUser;
    private Role mockRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setPassword("encodedPassword");

        mockRole = new Role();
        mockRole.setName(UserRoles.USER);
    }

    @Test
    void testFindById() {
        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(mockUser));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        verify(userRepository, times(1)).findByIdWithRoles(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.findById(1L));
    }

    @Test
    void testRegister() {
        UserRegisterDTO registerDTO = new UserRegisterDTO("John", "Doe","testuser", "john.doe@example.com", Gender.MALE, LocalDate.now(), "password", "password");
        when(userRepository.findByUsernameOrEmail("testuser", "john.doe@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName(UserRoles.USER)).thenReturn(Optional.of(mockRole));

        boolean result = userService.register(registerDTO);

        assertTrue(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterExistingUser() {
        UserRegisterDTO registerDTO = new UserRegisterDTO("John", "Doe","testuser", "john.doe@example.com", Gender.MALE, LocalDate.now(), "password", "password");
        when(userRepository.findByUsernameOrEmail("testuser", "john.doe@example.com")).thenReturn(Optional.of(mockUser));

        boolean result = userService.register(registerDTO);

        assertFalse(result);
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void testSearchUsers() {
        when(userRepository.findByFirstNameContainingOrLastNameContaining("John", "John")).thenReturn(List.of(mockUser));

        List<DisplayUserDTO> result = userService.searchUsers("John");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockUser.getUsername(), result.get(0).getUsername());
    }

    @Test
    void testGetDefaultProfilePicture() throws IOException {
        byte[] profilePicture = userService.getDefaultProfilePicture();

        assertNotNull(profilePicture);
        assertTrue(profilePicture.length > 0);
    }

    @Test
    void testGetPostPictures() throws IOException {
        List<byte[]> postPictures = userService.getPostPictures();

        assertNotNull(postPictures);
        assertFalse(postPictures.isEmpty());
        assertTrue(postPictures.get(0).length > 0);
    }

    @Test
    void testGetProfilePictures() throws IOException {
        List<byte[]> profilePictures = userService.getProfilePictures();

        assertNotNull(profilePictures);
        assertFalse(profilePictures.isEmpty());
        assertTrue(profilePictures.get(0).length > 0);
    }

    @Test
    void testSave() {
        userService.save(mockUser);

        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testFindAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(mockUser));

        var result = userService.findAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockUser.getId(), result.get(0).getId());
    }
}
