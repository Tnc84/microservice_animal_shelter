package com.tnc.userManagement.controller;

import com.tnc.userManagement.controller.dto.UserDTO;
import com.tnc.userManagement.controller.dtoMapper.UserDTOMapper;
import com.tnc.userManagement.service.IUserService;
import com.tnc.userManagement.service.exception.EmailExistException;
import com.tnc.userManagement.service.exception.EmailNotFoundException;
import com.tnc.userManagement.service.exception.UserNotFoundException;
import com.tnc.userManagement.service.exception.UsernameExistException;
import com.tnc.userManagement.service.model.HttpResponse;
import com.tnc.userManagement.service.model.UserDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.mail.MessagingException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserController
 * Tests all REST endpoints, validation, and error handling
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private IUserService userService;

    @Mock
    private UserDTOMapper userDTOMapper;

    @InjectMocks
    private UserController userController;

    private UserDTO testUserDTO;
    private UserDomain testUserDomain;

    @BeforeEach
    void setUp() {
        testUserDTO = new UserDTO(
            1L, "test123", "John", "Doe", "john.doe@example.com",
            "1234567890", "password123", new Date(), new Date(), new Date(),
            "ROLE_USER", new String[]{"USER:READ"}, true, true
        );

        testUserDomain = new UserDomain();
        testUserDomain.setId(1L);
        testUserDomain.setUserId("test123");
        testUserDomain.setFirstName("John");
        testUserDomain.setLastName("Doe");
        testUserDomain.setEmail("john.doe@example.com");
        testUserDomain.setRole("ROLE_USER");
        testUserDomain.setActive(true);
        testUserDomain.setNotLocked(true);
    }

    @Test
    void addNewUser_WithValidData_ShouldReturnCreatedUser() {
        // Arrange
        when(userService.addNewUserWithSpecificRole(
                anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
                .thenReturn(testUserDomain);
        when(userDTOMapper.toDTO(testUserDomain)).thenReturn(testUserDTO);

        // Act
        ResponseEntity<UserDTO> response = userController.addNewUser(testUserDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().firstName());
        verify(userService).addNewUserWithSpecificRole(
                testUserDTO.firstName(), testUserDTO.lastName(), testUserDTO.email(),
                testUserDTO.role(), testUserDTO.isNotLocked(), testUserDTO.isActive());
        verify(userDTOMapper).toDTO(testUserDomain);
    }

    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, EmailNotFoundException {
        // Arrange
        when(userService.updateUser(
                anyLong(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
                .thenReturn(testUserDomain);
        when(userDTOMapper.toDTO(testUserDomain)).thenReturn(testUserDTO);

        // Act
        ResponseEntity<UserDTO> response = userController.updateUser(testUserDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().firstName());
        verify(userService).updateUser(
                testUserDTO.id(), testUserDTO.firstName(), testUserDTO.lastName(),
                testUserDTO.email(), testUserDTO.role(), testUserDTO.isNotLocked(), testUserDTO.isActive());
        verify(userDTOMapper).toDTO(testUserDomain);
    }

    @Test
    void updateUser_WithEmailExists_ShouldThrowException() throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, EmailNotFoundException {
        // Arrange
        when(userService.updateUser(
                anyLong(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
                .thenThrow(new EmailExistException("Email already exists"));

        // Act & Assert
        assertThrows(EmailExistException.class, () -> userController.updateUser(testUserDTO));
        verify(userService).updateUser(
                testUserDTO.id(), testUserDTO.firstName(), testUserDTO.lastName(),
                testUserDTO.email(), testUserDTO.role(), testUserDTO.isNotLocked(), testUserDTO.isActive());
    }

    @Test
    void getUser_WithValidUsername_ShouldReturnUser() {
        // Arrange
        when(userService.findByEmail("john.doe@example.com")).thenReturn(testUserDomain);
        when(userDTOMapper.toDTO(testUserDomain)).thenReturn(testUserDTO);

        // Act
        ResponseEntity<UserDTO> response = userController.getUser("john.doe@example.com");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("john.doe@example.com", response.getBody().email());
        verify(userService).findByEmail("john.doe@example.com");
        verify(userDTOMapper).toDTO(testUserDomain);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<UserDomain> userDomains = Arrays.asList(testUserDomain);
        List<UserDTO> expectedDTOs = Arrays.asList(testUserDTO);
        
        when(userService.getAll()).thenReturn(userDomains);
        when(userDTOMapper.toDTOList(userDomains)).thenReturn(expectedDTOs);

        // Act
        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(userService).getAll();
        verify(userDTOMapper).toDTOList(userDomains);
    }

    @Test
    void resetPassword_WithValidEmail_ShouldReturnSuccessMessage() throws EmailNotFoundException, MessagingException {
        // Arrange
        doNothing().when(userService).resetPassword("john.doe@example.com");

        // Act
        ResponseEntity<HttpResponse> response = userController.resetPassword("john.doe@example.com");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("AN EMAIL WITH A NEW PASSWORD WAS SENT TO: JOHN.DOE@EXAMPLE.COM", response.getBody().getMessage());
        verify(userService).resetPassword("john.doe@example.com");
    }

    @Test
    void resetPassword_WithInvalidEmail_ShouldThrowException() throws EmailNotFoundException, MessagingException {
        // Arrange
        doThrow(new EmailNotFoundException("Email not found")).when(userService).resetPassword("invalid@example.com");

        // Act & Assert
        assertThrows(EmailNotFoundException.class, () -> userController.resetPassword("invalid@example.com"));
        verify(userService).resetPassword("invalid@example.com");
    }

    @Test
    void deleteUser_WithValidId_ShouldReturnSuccessMessage() {
        // Arrange
        doNothing().when(userService).deleteUser(1L);

        // Act
        ResponseEntity<HttpResponse> response = userController.deleteUser(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("USER DELETED SUCCESSFULLY.", response.getBody().getMessage());
        verify(userService).deleteUser(1L);
    }

    @Test
    void response_ShouldCreateProperHttpResponse() throws MessagingException, EmailNotFoundException {
        // Act - Test through resetPassword which uses response() internally
        doNothing().when(userService).resetPassword("test@example.com");
        ResponseEntity<HttpResponse> response = userController.resetPassword("test@example.com");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getHttpStatusCode());
        assertEquals("OK", response.getBody().getHttpStatus().getReasonPhrase());
        assertTrue(response.getBody().getMessage().contains("EMAIL"));
    }
}
