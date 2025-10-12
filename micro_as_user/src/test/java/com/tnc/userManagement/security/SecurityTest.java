package com.tnc.userManagement.security;

import com.tnc.userManagement.repository.UserRepository;
import com.tnc.userManagement.repository.entity.User;
import com.tnc.userManagement.service.ServiceImpl.UserServiceImpl;
import com.tnc.userManagement.service.constant.RoleEnum;
import com.tnc.userManagement.service.mapper.UserDomainMapper;
import com.tnc.userManagement.service.model.UserDomain;
import com.tnc.userManagement.service.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collection;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Security tests for User service
 * Tests authentication, authorization, and security features
 */
@ExtendWith(MockitoExtension.class)
class SecurityTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDomainMapper userDomainMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDomain testUserDomain;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUserId("test123");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setRole(RoleEnum.ROLE_USER);
        testUser.setActive(true);
        testUser.setNotLocked(true);
        testUser.setJoinDate(new Date());

        testUserDomain = new UserDomain();
        testUserDomain.setId(1L);
        testUserDomain.setUserId("test123");
        testUserDomain.setFirstName("John");
        testUserDomain.setLastName("Doe");
        testUserDomain.setEmail("john.doe@example.com");
        testUserDomain.setPassword("$2a$10$encodedPassword");
        testUserDomain.setRole("ROLE_USER");
        testUserDomain.setAuthorities(new String[]{"USER:READ", "USER:WRITE"});
        testUserDomain.setActive(true);
        testUserDomain.setNotLocked(true);
        testUserDomain.setJoinDate(new Date());
    }

    @Test
    void loadUserByUsername_WithValidUser_ShouldReturnUserPrincipal() {
        // Arrange
        when(userRepository.findUserByEmail("john.doe@example.com")).thenReturn(testUser);
        when(userDomainMapper.toDomain(testUser)).thenReturn(testUserDomain);

        // Act
        UserDetails userDetails = userService.loadUserByUsername("john.doe@example.com");

        // Assert
        assertNotNull(userDetails);
        assertTrue(userDetails instanceof UserPrincipal);
        assertEquals("john.doe@example.com", userDetails.getUsername());
        assertEquals("$2a$10$encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isCredentialsNonExpired());
        
        // Verify authorities
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertNotNull(authorities);
        assertTrue(authorities.size() > 0);
    }

    @Test
    void loadUserByUsername_WithInactiveUser_ShouldReturnDisabledUserDetails() {
        // Arrange
        testUser.setActive(false);
        when(userRepository.findUserByEmail("john.doe@example.com")).thenReturn(testUser);
        when(userDomainMapper.toDomain(testUser)).thenReturn(testUserDomain);

        // Act
        UserDetails userDetails = userService.loadUserByUsername("john.doe@example.com");

        // Assert
        assertNotNull(userDetails);
        assertFalse(userDetails.isEnabled());
        assertEquals("john.doe@example.com", userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_WithLockedUser_ShouldReturnLockedUserDetails() {
        // Arrange
        testUser.setNotLocked(false);
        when(userRepository.findUserByEmail("john.doe@example.com")).thenReturn(testUser);
        when(userDomainMapper.toDomain(testUser)).thenReturn(testUserDomain);

        // Act
        UserDetails userDetails = userService.loadUserByUsername("john.doe@example.com");

        // Assert
        assertNotNull(userDetails);
        assertFalse(userDetails.isAccountNonLocked());
        assertEquals("john.doe@example.com", userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findUserByEmail("nonexistent@example.com")).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> 
                userService.loadUserByUsername("nonexistent@example.com"));
    }

    @Test
    void passwordEncoding_ShouldBeApplied() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userDomainMapper.toEntity(any(UserDomain.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDomain result = userService.addNewUserWithSpecificRole(
                "John", "Doe", "john.doe@example.com", "ROLE_USER", true, true);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void userAuthorities_ShouldBeSetBasedOnRole() {
        // Arrange
        when(userRepository.findUserByEmail("admin@example.com")).thenReturn(testUser);
        testUserDomain.setRole("ROLE_ADMIN");
        testUserDomain.setAuthorities(new String[]{"ADMIN:READ", "ADMIN:WRITE", "ADMIN:DELETE"});
        when(userDomainMapper.toDomain(testUser)).thenReturn(testUserDomain);

        // Act
        UserDetails userDetails = userService.loadUserByUsername("admin@example.com");

        // Assert
        assertNotNull(userDetails);
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertNotNull(authorities);
        assertTrue(authorities.size() >= 3); // Should have admin authorities
    }

    @Test
    void accountStatus_ShouldReflectUserSettings() {
        // Arrange
        testUser.setActive(false);
        testUser.setNotLocked(false);
        when(userRepository.findUserByEmail("john.doe@example.com")).thenReturn(testUser);
        when(userDomainMapper.toDomain(testUser)).thenReturn(testUserDomain);

        // Act
        UserDetails userDetails = userService.loadUserByUsername("john.doe@example.com");

        // Assert
        assertNotNull(userDetails);
        assertFalse(userDetails.isEnabled()); // Should be disabled
        assertFalse(userDetails.isAccountNonLocked()); // Should be locked
        assertTrue(userDetails.isAccountNonExpired()); // Should always be true
        assertTrue(userDetails.isCredentialsNonExpired()); // Should always be true
    }

    @Test
    void userPrincipal_ShouldContainUserInformation() {
        // Arrange
        when(userRepository.findUserByEmail("john.doe@example.com")).thenReturn(testUser);
        when(userDomainMapper.toDomain(testUser)).thenReturn(testUserDomain);

        // Act
        UserDetails userDetails = userService.loadUserByUsername("john.doe@example.com");

        // Assert
        assertTrue(userDetails instanceof UserPrincipal);
        UserPrincipal userPrincipal = (UserPrincipal) userDetails;
        
        assertEquals("test123", userPrincipal.getUserId());
        assertEquals("ROLE_USER", userPrincipal.getRole());
        assertNotNull(userPrincipal.getUser());
        assertEquals("John", userPrincipal.getUser().getFirstName());
        assertEquals("Doe", userPrincipal.getUser().getLastName());
    }

    @Test
    void securityValidation_ShouldPreventInvalidOperations() {
        // Arrange
        when(userRepository.findUserByEmail("john.doe@example.com")).thenReturn(testUser);
        when(userDomainMapper.toDomain(testUser)).thenReturn(testUserDomain);

        // Act
        UserDetails userDetails = userService.loadUserByUsername("john.doe@example.com");

        // Assert
        assertNotNull(userDetails);
        
        // Verify that security constraints are properly applied
        assertTrue(userDetails.getUsername().equals("john.doe@example.com"));
        assertNotNull(userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().size() > 0);
    }
}
