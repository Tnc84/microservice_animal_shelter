package com.tnc.userManagement.controller.dtoMapper;

import com.tnc.userManagement.controller.dto.UserDTO;
import com.tnc.userManagement.service.model.UserDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserDTOMapper
 * Tests MapStruct mapping between UserDTO and UserDomain
 */
@ExtendWith(MockitoExtension.class)
class UserDTOMapperTest {

    private UserDTOMapper mapper;
    private UserDTO testUserDTO;
    private UserDomain testUserDomain;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserDTOMapper.class);
        
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
        testUserDomain.setPhone("1234567890");
        testUserDomain.setPassword("password123");
        testUserDomain.setLastLoginDate(new Date());
        testUserDomain.setLastLoginDateDisplay(new Date());
        testUserDomain.setJoinDate(new Date());
        testUserDomain.setRole("ROLE_USER");
        testUserDomain.setAuthorities(new String[]{"USER:READ"});
        testUserDomain.setActive(true);
        testUserDomain.setNotLocked(true);
    }

    @Test
    void toDTO_WithValidUserDomain_ShouldMapCorrectly() {
        // Act
        UserDTO result = mapper.toDTO(testUserDomain);

        // Assert
        assertNotNull(result);
        assertEquals(testUserDomain.getId(), result.id());
        assertEquals(testUserDomain.getUserId(), result.userId());
        assertEquals(testUserDomain.getFirstName(), result.firstName());
        assertEquals(testUserDomain.getLastName(), result.lastName());
        assertEquals(testUserDomain.getEmail(), result.email());
        assertEquals(testUserDomain.getPhone(), result.phone());
        assertEquals(testUserDomain.getPassword(), result.password());
        assertEquals(testUserDomain.getLastLoginDate(), result.lastLoginDate());
        assertEquals(testUserDomain.getLastLoginDateDisplay(), result.lastLoginDateDisplay());
        assertEquals(testUserDomain.getJoinDate(), result.joinDate());
        assertEquals(testUserDomain.getRole(), result.role());
        assertArrayEquals(testUserDomain.getAuthorities(), result.authorities());
        assertEquals(testUserDomain.isActive(), result.isActive());
        assertEquals(testUserDomain.isNotLocked(), result.isNotLocked());
    }

    @Test
    void toDomain_WithValidUserDTO_ShouldMapCorrectly() {
        // Act
        UserDomain result = mapper.toDomain(testUserDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testUserDTO.id(), result.getId());
        assertEquals(testUserDTO.userId(), result.getUserId());
        assertEquals(testUserDTO.firstName(), result.getFirstName());
        assertEquals(testUserDTO.lastName(), result.getLastName());
        assertEquals(testUserDTO.email(), result.getEmail());
        assertEquals(testUserDTO.phone(), result.getPhone());
        assertEquals(testUserDTO.password(), result.getPassword());
        assertEquals(testUserDTO.lastLoginDate(), result.getLastLoginDate());
        assertEquals(testUserDTO.lastLoginDateDisplay(), result.getLastLoginDateDisplay());
        assertEquals(testUserDTO.joinDate(), result.getJoinDate());
        assertEquals(testUserDTO.role(), result.getRole());
        assertArrayEquals(testUserDTO.authorities(), result.getAuthorities());
        assertEquals(testUserDTO.isActive(), result.isActive());
        assertEquals(testUserDTO.isNotLocked(), result.isNotLocked());
    }

    @Test
    void toDTOList_WithValidUserDomainList_ShouldMapCorrectly() {
        // Arrange
        List<UserDomain> userDomains = Arrays.asList(testUserDomain);

        // Act
        List<UserDTO> result = mapper.toDTOList(userDomains);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUserDomain.getId(), result.get(0).id());
        assertEquals(testUserDomain.getFirstName(), result.get(0).firstName());
        assertEquals(testUserDomain.getEmail(), result.get(0).email());
    }

    @Test
    void toDomainList_WithValidUserDTOList_ShouldMapCorrectly() {
        // Arrange
        List<UserDTO> userDTOs = Arrays.asList(testUserDTO);

        // Act
        List<UserDomain> result = mapper.toDomainList(userDTOs);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUserDTO.id(), result.get(0).getId());
        assertEquals(testUserDTO.firstName(), result.get(0).getFirstName());
        assertEquals(testUserDTO.email(), result.get(0).getEmail());
    }

    @Test
    void toDTO_WithNullUserDomain_ShouldReturnNull() {
        // Act
        UserDTO result = mapper.toDTO(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDomain_WithNullUserDTO_ShouldReturnNull() {
        // Act
        UserDomain result = mapper.toDomain(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDTOList_WithNullList_ShouldReturnNull() {
        // Act
        List<UserDTO> result = mapper.toDTOList(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDomainList_WithNullList_ShouldReturnNull() {
        // Act
        List<UserDomain> result = mapper.toDomainList(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDTO_WithEmptyUserDomain_ShouldMapCorrectly() {
        // Arrange
        UserDomain emptyUserDomain = new UserDomain();

        // Act
        UserDTO result = mapper.toDTO(emptyUserDomain);

        // Assert
        assertNotNull(result);
        assertNull(result.id());
        assertNull(result.userId());
        assertNull(result.firstName());
        assertNull(result.lastName());
        assertNull(result.email());
    }

    @Test
    void toDomain_WithEmptyUserDTO_ShouldMapCorrectly() {
        // Arrange
        UserDTO emptyUserDTO = new UserDTO(
            null, null, null, null, null,
            null, null, null, null, null,
            null, null, false, false
        );

        // Act
        UserDomain result = mapper.toDomain(emptyUserDTO);

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getUserId());
        assertNull(result.getFirstName());
        assertNull(result.getLastName());
        assertNull(result.getEmail());
    }

    @Test
    void toDTOList_WithEmptyList_ShouldReturnEmptyList() {
        // Arrange
        List<UserDomain> emptyUserDomains = Arrays.asList();

        // Act
        List<UserDTO> result = mapper.toDTOList(emptyUserDomains);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDomainList_WithEmptyList_ShouldReturnEmptyList() {
        // Arrange
        List<UserDTO> emptyUserDTOs = Arrays.asList();

        // Act
        List<UserDomain> result = mapper.toDomainList(emptyUserDTOs);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDTO_WithDifferentRole_ShouldMapCorrectly() {
        // Arrange
        testUserDomain.setRole("ROLE_ADMIN");
        testUserDomain.setAuthorities(new String[]{"ADMIN:READ", "ADMIN:WRITE"});

        // Act
        UserDTO result = mapper.toDTO(testUserDomain);

        // Assert
        assertNotNull(result);
        assertEquals("ROLE_ADMIN", result.role());
        assertArrayEquals(new String[]{"ADMIN:READ", "ADMIN:WRITE"}, result.authorities());
    }

    @Test
    void toDomain_WithDifferentRole_ShouldMapCorrectly() {
        // Arrange
        UserDTO adminUserDTO = new UserDTO(
            1L, "admin123", "Admin", "User", "admin@example.com",
            "1234567890", "password123", new Date(), new Date(), new Date(),
            "ROLE_ADMIN", new String[]{"ADMIN:READ", "ADMIN:WRITE"}, true, true
        );

        // Act
        UserDomain result = mapper.toDomain(adminUserDTO);

        // Assert
        assertNotNull(result);
        assertEquals("ROLE_ADMIN", result.getRole());
        assertArrayEquals(new String[]{"ADMIN:READ", "ADMIN:WRITE"}, result.getAuthorities());
    }

    @Test
    void toDTOList_WithMultipleUserDomains_ShouldMapCorrectly() {
        // Arrange
        UserDomain userDomain1 = new UserDomain();
        userDomain1.setId(1L);
        userDomain1.setFirstName("User1");
        userDomain1.setEmail("user1@example.com");

        UserDomain userDomain2 = new UserDomain();
        userDomain2.setId(2L);
        userDomain2.setFirstName("User2");
        userDomain2.setEmail("user2@example.com");

        List<UserDomain> userDomains = Arrays.asList(userDomain1, userDomain2);

        // Act
        List<UserDTO> result = mapper.toDTOList(userDomains);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("User1", result.get(0).firstName());
        assertEquals("User2", result.get(1).firstName());
    }

    @Test
    void toDomainList_WithMultipleUserDTOs_ShouldMapCorrectly() {
        // Arrange
        UserDTO userDTO1 = new UserDTO(
            1L, "user1", "User1", "Test1", "user1@example.com",
            "1234567890", "password", new Date(), new Date(), new Date(),
            "ROLE_USER", new String[]{"USER:READ"}, true, true
        );

        UserDTO userDTO2 = new UserDTO(
            2L, "user2", "User2", "Test2", "user2@example.com",
            "1234567890", "password", new Date(), new Date(), new Date(),
            "ROLE_USER", new String[]{"USER:READ"}, true, true
        );

        List<UserDTO> userDTOs = Arrays.asList(userDTO1, userDTO2);

        // Act
        List<UserDomain> result = mapper.toDomainList(userDTOs);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("User1", result.get(0).getFirstName());
        assertEquals("User2", result.get(1).getFirstName());
    }
}