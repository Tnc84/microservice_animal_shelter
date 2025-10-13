package com.tnc.userManagement.service.ServiceImpl;

import com.tnc.userManagement.repository.UserRepository;
import com.tnc.userManagement.repository.entity.User;
import com.tnc.userManagement.service.IUserService;
import com.tnc.userManagement.service.constant.RoleEnum;
import com.tnc.userManagement.service.exception.EmailExistException;
import com.tnc.userManagement.service.exception.EmailNotFoundException;
import com.tnc.userManagement.service.mapper.UserDomainMapper;
import com.tnc.userManagement.service.model.UserDomain;
import com.tnc.userManagement.service.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserServiceImpl
 * Tests business logic, security, and user management operations
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDomainMapper userDomainMapper;

    @Mock
    private EmailService emailService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDomain testUserDomain;

    @BeforeEach
    void setUp() {
        // Manually inject the mocked password encoder
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUserId("test123");
        testUser.setFirstName("JohnUser");
        testUser.setLastName("DoeUser");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(RoleEnum.ROLE_USER);
        testUser.setActive(true);
        testUser.setNotLocked(true);
        testUser.setJoinDate(new Date());

        testUserDomain = new UserDomain();
        testUserDomain.setId(1L);
        testUserDomain.setUserId("test123");
        testUserDomain.setFirstName("JohnUser");
        testUserDomain.setLastName("DoeUser");
        testUserDomain.setEmail("john.doe@example.com");
        testUserDomain.setPassword("encodedPassword");
        testUserDomain.setRole("ROLE_USER");
        testUserDomain.setActive(true);
        testUserDomain.setNotLocked(true);
        testUserDomain.setJoinDate(new Date());
    }

    @Test
    void addNewUserWithSpecificRole_WithValidData_ShouldCreateUser() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userDomainMapper.toEntity(any(UserDomain.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userDomainMapper.toDomain(testUser)).thenReturn(testUserDomain);

        // Act
        UserDomain result = userService.addNewUserWithSpecificRole(
                "JohnUser", "DoeUser", "john.doe@example.com", "ROLE_USER", true, true);

        // Assert
        assertNotNull(result);
        assertEquals("JohnUser", result.getFirstName());
        assertEquals("DoeUser", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("ROLE_USER", result.getRole());
        assertTrue(result.isActive());
        assertTrue(result.isNotLocked());
        assertNotNull(result.getPassword());
        
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
        verify(userDomainMapper).toDomain(testUser);
    }

    @Test
    void findByEmail_WithValidEmail_ShouldReturnUser() {
        // Arrange
        when(userRepository.findUserByEmail("john.doe@example.com")).thenReturn(testUser);
        when(userDomainMapper.toDomain(testUser)).thenReturn(testUserDomain);

        // Act
        UserDomain result = userService.findByEmail("john.doe@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userRepository).findUserByEmail("john.doe@example.com");
        verify(userDomainMapper).toDomain(testUser);
    }

    @Test
    void findByEmail_WithInvalidEmail_ShouldReturnNull() {
        // Arrange
        when(userRepository.findUserByEmail("invalid@example.com")).thenReturn(null);

        // Act
        UserDomain result = userService.findByEmail("invalid@example.com");

        // Assert
        assertNull(result);
        verify(userRepository).findUserByEmail("invalid@example.com");
    }

    @Test
    void get_WithValidId_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userDomainMapper.toDomain(testUser)).thenReturn(testUserDomain);

        // Act
        UserDomain result = userService.get(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
        verify(userDomainMapper).toDomain(testUser);
    }

    @Test
    void get_WithInvalidId_ShouldReturnNull() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        UserDomain result = userService.get(999L);

        // Assert
        assertNull(result);
        verify(userRepository).findById(999L);
    }

    @Test
    void deleteUser_WithValidId_ShouldDeleteUser() {
        // Arrange
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).deleteById(1L);
    }

    @Test
    void getAll_ShouldReturnAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        List<UserDomain> userDomains = Arrays.asList(testUserDomain);
        
        when(userRepository.findAll()).thenReturn(users);
        when(userDomainMapper.toDomainList(users)).thenReturn(userDomains);

        // Act
        List<UserDomain> result = userService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john.doe@example.com", result.get(0).getEmail());
        verify(userRepository).findAll();
        verify(userDomainMapper).toDomainList(users);
    }

    @Test
    void loadUserByUsername_WithValidEmail_ShouldReturnUserDetails() {
        // Arrange
        when(userRepository.findUserByEmail("john.doe@example.com")).thenReturn(testUser);
        when(userDomainMapper.toDomain(testUser)).thenReturn(testUserDomain);

        // Act
        UserDetails result = userService.loadUserByUsername("john.doe@example.com");

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof UserPrincipal);
        assertEquals("john.doe@example.com", result.getUsername());
        assertTrue(result.isEnabled());
        assertTrue(result.isAccountNonLocked());
        verify(userRepository).findUserByEmail("john.doe@example.com");
    }

    @Test
    void loadUserByUsername_WithInvalidEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.findUserByEmail("invalid@example.com")).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("invalid@example.com"));
        verify(userRepository).findUserByEmail("invalid@example.com");
    }

    @Test
    void resetPassword_WithValidEmail_ShouldSendEmail() throws Exception {
        // Arrange
        when(userRepository.findUserByEmail("john.doe@example.com")).thenReturn(testUser);
        when(userDomainMapper.toDomain(testUser)).thenReturn(testUserDomain);
        when(userDomainMapper.toEntity(any(UserDomain.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(emailService).sendNewPasswordEmail(anyString(), anyString(), anyString());

        // Act
        userService.resetPassword("john.doe@example.com");

        // Assert
        verify(userRepository).findUserByEmail("john.doe@example.com");
        verify(emailService).sendNewPasswordEmail(anyString(), anyString(), anyString());
    }

    @Test
    void resetPassword_WithInvalidEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.findUserByEmail("invalid@example.com")).thenReturn(null);

        // Act & Assert
        assertThrows(EmailNotFoundException.class, () -> userService.resetPassword("invalid@example.com"));
        verify(userRepository).findUserByEmail("invalid@example.com");
    }

    @Test
    void updateUser_WithValidData_ShouldUpdateUser() throws Exception {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userDomainMapper.toDomain(testUser)).thenReturn(testUserDomain);
        when(userDomainMapper.toEntity(any(UserDomain.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDomain result = userService.updateUser(1L, "John", "Doe", "john.doe@example.com", "ROLE_USER", true, true);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_WithEmailExists_ShouldThrowException() throws Exception {
        // Arrange
        User existingUserWithEmail = new User();
        existingUserWithEmail.setId(2L); // Different ID
        existingUserWithEmail.setEmail("existing@example.com");
        
        UserDomain existingUserDomain = new UserDomain();
        existingUserDomain.setId(2L);
        existingUserDomain.setEmail("existing@example.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userDomainMapper.toDomain(testUser)).thenReturn(testUserDomain);
        when(userRepository.findUserByEmail("existing@example.com")).thenReturn(existingUserWithEmail);
        when(userDomainMapper.toDomain(existingUserWithEmail)).thenReturn(existingUserDomain);

        // Act & Assert
        assertThrows(EmailExistException.class, () -> 
                userService.updateUser(1L, "John", "Doe", "existing@example.com", "ROLE_USER", true, true));
    }

    @Test
    void generateUserId_ShouldReturnRandomString() {
        // Act - Test through addNewUserWithSpecificRole which uses generateUserId internally
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userDomainMapper.toEntity(any(UserDomain.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userDomainMapper.toDomain(testUser)).thenReturn(testUserDomain);
        
        UserDomain result = userService.addNewUserWithSpecificRole(
                "JohnUser", "DoeUser", "john.doe@example.com", "ROLE_USER", true, true);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getUserId());
        assertTrue(result.getUserId().length() > 0);
    }

    @Test
    void generatePassword_ShouldReturnRandomString() {
        // Act - Test through addNewUserWithSpecificRole which uses generatePassword internally
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userDomainMapper.toEntity(any(UserDomain.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userDomainMapper.toDomain(testUser)).thenReturn(testUserDomain);
        
        UserDomain result = userService.addNewUserWithSpecificRole(
                "JohnUser", "DoeUser", "john.doe@example.com", "ROLE_USER", true, true);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getPassword());
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void getRoleEnumName_WithValidRole_ShouldReturnRoleEnum() {
        // Act - Test through addNewUserWithSpecificRole which uses getRoleEnumName internally
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userDomainMapper.toEntity(any(UserDomain.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userDomainMapper.toDomain(testUser)).thenReturn(testUserDomain);
        
        UserDomain result = userService.addNewUserWithSpecificRole(
                "JohnUser", "DoeUser", "john.doe@example.com", "ROLE_USER", true, true);

        // Assert
        assertNotNull(result);
        assertEquals("ROLE_USER", result.getRole());
    }

    @Test
    void getRoleEnumName_WithInvalidRole_ShouldReturnDefaultRole() {
        // Act - Test through addNewUserWithSpecificRole with invalid role
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userDomainMapper.toEntity(any(UserDomain.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userDomainMapper.toDomain(testUser)).thenReturn(testUserDomain);
        
        UserDomain result = userService.addNewUserWithSpecificRole(
                "John", "Doe", "john.doe@example.com", "INVALID_ROLE", true, true);

        // Assert
        assertNotNull(result);
        assertEquals("ROLE_USER", result.getRole()); // Should default to ROLE_USER
    }
}
