package com.tnc.userManagement.integration;

import com.tnc.userManagement.repository.UserRepository;
import com.tnc.userManagement.repository.entity.User;
import com.tnc.userManagement.service.IUserService;
import com.tnc.userManagement.service.model.UserDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for User service
 * Tests database interactions and service layer integration
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private UserDomain testUserDomain;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        userRepository.deleteAll();
        
        testUserDomain = new UserDomain();
        testUserDomain.setFirstName("Integration");
        testUserDomain.setLastName("Test");
        testUserDomain.setEmail("integration.test@example.com");
        testUserDomain.setRole("ROLE_USER");
        testUserDomain.setActive(true);
        testUserDomain.setNotLocked(true);
        testUserDomain.setJoinDate(new Date());
    }

    @Test
    void addNewUser_ShouldPersistToDatabase() {
        // Act
        UserDomain result = userService.addNewUserWithSpecificRole(
                testUserDomain.getFirstName(),
                testUserDomain.getLastName(),
                testUserDomain.getEmail(),
                testUserDomain.getRole(),
                testUserDomain.isActive(),
                testUserDomain.isNotLocked()
        );

        // Assert
        assertNotNull(result);
        assertEquals("Integration", result.getFirstName());
        assertEquals("Test", result.getLastName());
        assertEquals("integration.test@example.com", result.getEmail());
        assertNotNull(result.getUserId());
        assertNotNull(result.getPassword());
        
        // Verify persistence
        List<User> savedUsers = userRepository.findAll();
        assertEquals(1, savedUsers.size());
        assertEquals("integration.test@example.com", savedUsers.get(0).getEmail());
    }

    @Test
    void findByEmail_ShouldReturnPersistedUser() {
        // Arrange - Add user first
        userService.addNewUserWithSpecificRole(
                testUserDomain.getFirstName(),
                testUserDomain.getLastName(),
                testUserDomain.getEmail(),
                testUserDomain.getRole(),
                testUserDomain.isActive(),
                testUserDomain.isNotLocked()
        );

        // Act
        UserDomain result = userService.findByEmail("integration.test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("integration.test@example.com", result.getEmail());
        assertEquals("Integration", result.getFirstName());
        assertEquals("Test", result.getLastName());
    }

    @Test
    void getAllUsers_ShouldReturnAllPersistedUsers() {
        // Arrange - Add multiple users
        userService.addNewUserWithSpecificRole("User1", "Test1", "user1@example.com", "ROLE_USER", true, true);
        userService.addNewUserWithSpecificRole("User2", "Test2", "user2@example.com", "ROLE_ADMIN", true, true);

        // Act
        List<UserDomain> result = userService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("user1@example.com")));
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("user2@example.com")));
    }

    @Test
    void deleteUser_ShouldRemoveFromDatabase() {
        // Arrange - Add user first
        UserDomain addedUser = userService.addNewUserWithSpecificRole(
                testUserDomain.getFirstName(),
                testUserDomain.getLastName(),
                testUserDomain.getEmail(),
                testUserDomain.getRole(),
                testUserDomain.isActive(),
                testUserDomain.isNotLocked()
        );

        // Verify user exists
        List<User> usersBefore = userRepository.findAll();
        assertEquals(1, usersBefore.size());

        // Act
        userService.deleteUser(addedUser.getId());

        // Assert
        List<User> usersAfter = userRepository.findAll();
        assertEquals(0, usersAfter.size());
    }

    @Test
    void updateUser_ShouldModifyExistingUser() throws Exception {
        // Arrange - Add user first
        UserDomain addedUser = userService.addNewUserWithSpecificRole(
                testUserDomain.getFirstName(),
                testUserDomain.getLastName(),
                testUserDomain.getEmail(),
                testUserDomain.getRole(),
                testUserDomain.isActive(),
                testUserDomain.isNotLocked()
        );

        // Act - Update user
        UserDomain updatedUser = userService.updateUser(
                addedUser.getId(),
                "UpdatedFirstName",
                "UpdatedLastName",
                "updated@example.com",
                "ROLE_ADMIN",
                false,
                false
        );

        // Assert
        assertNotNull(updatedUser);
        assertEquals("UpdatedFirstName", updatedUser.getFirstName());
        assertEquals("UpdatedLastName", updatedUser.getLastName());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("ROLE_ADMIN", updatedUser.getRole());
        assertFalse(updatedUser.isActive());
        assertFalse(updatedUser.isNotLocked());
    }

    @Test
    void passwordEncryption_ShouldBeApplied() {
        // Act
        UserDomain result = userService.addNewUserWithSpecificRole(
                testUserDomain.getFirstName(),
                testUserDomain.getLastName(),
                testUserDomain.getEmail(),
                testUserDomain.getRole(),
                testUserDomain.isActive(),
                testUserDomain.isNotLocked()
        );

        // Assert
        assertNotNull(result.getPassword());
        assertTrue(passwordEncoder.matches("generatedPassword", result.getPassword()) || 
                  result.getPassword().length() > 10); // BCrypt produces long hashes
    }

    @Test
    void userRoleAndAuthorities_ShouldBeSetCorrectly() {
        // Act
        UserDomain result = userService.addNewUserWithSpecificRole(
                testUserDomain.getFirstName(),
                testUserDomain.getLastName(),
                testUserDomain.getEmail(),
                "ROLE_ADMIN",
                testUserDomain.isActive(),
                testUserDomain.isNotLocked()
        );

        // Assert
        assertEquals("ROLE_ADMIN", result.getRole());
        assertNotNull(result.getAuthorities());
        assertTrue(result.getAuthorities().length > 0);
    }

    @Test
    void userTimestamps_ShouldBeSetCorrectly() {
        // Arrange
        Date beforeCreation = new Date();

        // Act
        UserDomain result = userService.addNewUserWithSpecificRole(
                testUserDomain.getFirstName(),
                testUserDomain.getLastName(),
                testUserDomain.getEmail(),
                testUserDomain.getRole(),
                testUserDomain.isActive(),
                testUserDomain.isNotLocked()
        );

        // Assert
        assertNotNull(result.getJoinDate());
        assertTrue(result.getJoinDate().after(beforeCreation) || result.getJoinDate().equals(beforeCreation));
    }

    @Test
    void findByEmail_WithNonExistentEmail_ShouldReturnNull() {
        // Act
        UserDomain result = userService.findByEmail("nonexistent@example.com");

        // Assert
        assertNull(result);
    }

    @Test
    void get_WithNonExistentId_ShouldReturnNull() {
        // Act
        UserDomain result = userService.get(999L);

        // Assert
        assertNull(result);
    }
}
