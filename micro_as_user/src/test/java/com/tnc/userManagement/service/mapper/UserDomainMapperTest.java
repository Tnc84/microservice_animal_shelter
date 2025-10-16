package com.tnc.userManagement.service.mapper;

import com.tnc.userManagement.repository.entity.User;
import com.tnc.userManagement.service.constant.RoleEnum;
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
 * Unit tests for UserDomainMapper
 * Tests MapStruct mapping between User entity and UserDomain
 */
@ExtendWith(MockitoExtension.class)
class UserDomainMapperTest {

    private UserDomainMapper mapper;
    private User testUser;
    private UserDomain testUserDomain;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserDomainMapper.class);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUserId("test123");
        testUser.setFirstName("JohnUser");
        testUser.setLastName("DoeUser");
        testUser.setEmail("john.doe@example.com");
        testUser.setPhone("1234567890");
        testUser.setPassword("encodedPassword");
        testUser.setLastLoginDate(new Date());
        testUser.setLastLoginDateDisplay(new Date());
        testUser.setJoinDate(new Date());
        testUser.setRole(RoleEnum.ROLE_USER);
        testUser.setActive(true);
        testUser.setNotLocked(true);

        testUserDomain = new UserDomain();
        testUserDomain.setId(1L);
        testUserDomain.setUserId("test123");
        testUserDomain.setFirstName("JohnUser");
        testUserDomain.setLastName("DoeUser");
        testUserDomain.setEmail("john.doe@example.com");
        testUserDomain.setPhone("1234567890");
        testUserDomain.setPassword("encodedPassword");
        testUserDomain.setLastLoginDate(new Date());
        testUserDomain.setLastLoginDateDisplay(new Date());
        testUserDomain.setJoinDate(new Date());
        testUserDomain.setRole("ROLE_USER");
        testUserDomain.setAuthorities(new String[]{"USER:READ"});
        testUserDomain.setActive(true);
        testUserDomain.setNotLocked(true);
    }

    @Test
    void toDomain_WithValidUser_ShouldMapCorrectly() {
        // Act
        UserDomain result = mapper.toDomain(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUserId(), result.getUserId());
        assertEquals(testUser.getFirstName(), result.getFirstName());
        assertEquals(testUser.getLastName(), result.getLastName());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getPhone(), result.getPhone());
        assertEquals(testUser.getPassword(), result.getPassword());
        assertEquals(testUser.getLastLoginDate(), result.getLastLoginDate());
        assertEquals(testUser.getLastLoginDateDisplay(), result.getLastLoginDateDisplay());
        assertEquals(testUser.getJoinDate(), result.getJoinDate());
        assertEquals(testUser.getRole().name(), result.getRole());
        assertEquals(testUser.isActive(), result.isActive());
        assertEquals(testUser.isNotLocked(), result.isNotLocked());
    }

    @Test
    void toEntity_WithValidUserDomain_ShouldMapCorrectly() {
        // Act
        User result = mapper.toEntity(testUserDomain);

        // Assert
        assertNotNull(result);
        assertEquals(testUserDomain.getId(), result.getId());
        assertEquals(testUserDomain.getUserId(), result.getUserId());
        assertEquals(testUserDomain.getFirstName(), result.getFirstName());
        assertEquals(testUserDomain.getLastName(), result.getLastName());
        assertEquals(testUserDomain.getEmail(), result.getEmail());
        assertEquals(testUserDomain.getPhone(), result.getPhone());
        assertEquals(testUserDomain.getPassword(), result.getPassword());
        assertEquals(testUserDomain.getLastLoginDate(), result.getLastLoginDate());
        assertEquals(testUserDomain.getLastLoginDateDisplay(), result.getLastLoginDateDisplay());
        assertEquals(testUserDomain.getJoinDate(), result.getJoinDate());
        assertEquals(RoleEnum.valueOf(testUserDomain.getRole()), result.getRole());
        assertEquals(testUserDomain.isActive(), result.isActive());
        assertEquals(testUserDomain.isNotLocked(), result.isNotLocked());
    }

    @Test
    void toDomainList_WithValidUserList_ShouldMapCorrectly() {
        // Arrange
        List<User> users = Arrays.asList(testUser);

        // Act
        List<UserDomain> result = mapper.toDomainList(users);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
        assertEquals(testUser.getFirstName(), result.get(0).getFirstName());
        assertEquals(testUser.getEmail(), result.get(0).getEmail());
    }

    @Test
    void toEntityList_WithValidUserDomainList_ShouldMapCorrectly() {
        // Arrange
        List<UserDomain> userDomains = Arrays.asList(testUserDomain);

        // Act
        List<User> result = mapper.toEntityList(userDomains);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUserDomain.getId(), result.get(0).getId());
        assertEquals(testUserDomain.getFirstName(), result.get(0).getFirstName());
        assertEquals(testUserDomain.getEmail(), result.get(0).getEmail());
    }

    @Test
    void toDomain_WithNullUser_ShouldReturnNull() {
        // Act
        UserDomain result = mapper.toDomain(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toEntity_WithNullUserDomain_ShouldReturnNull() {
        // Act
        User result = mapper.toEntity(null);

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
    void toEntityList_WithNullList_ShouldReturnNull() {
        // Act
        List<User> result = mapper.toEntityList(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDomain_WithEmptyUser_ShouldMapCorrectly() {
        // Arrange
        User emptyUser = new User();

        // Act
        UserDomain result = mapper.toDomain(emptyUser);

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getUserId());
        assertNull(result.getFirstName());
        assertNull(result.getLastName());
        assertNull(result.getEmail());
    }

    @Test
    void toEntity_WithEmptyUserDomain_ShouldMapCorrectly() {
        // Arrange
        UserDomain emptyUserDomain = new UserDomain();

        // Act
        User result = mapper.toEntity(emptyUserDomain);

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getUserId());
        assertNull(result.getFirstName());
        assertNull(result.getLastName());
        assertNull(result.getEmail());
    }

    @Test
    void toDomainList_WithEmptyList_ShouldReturnEmptyList() {
        // Arrange
        List<User> emptyUsers = Arrays.asList();

        // Act
        List<UserDomain> result = mapper.toDomainList(emptyUsers);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toEntityList_WithEmptyList_ShouldReturnEmptyList() {
        // Arrange
        List<UserDomain> emptyUserDomains = Arrays.asList();

        // Act
        List<User> result = mapper.toEntityList(emptyUserDomains);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDomain_WithDifferentRole_ShouldMapCorrectly() {
        // Arrange
        testUser.setRole(RoleEnum.ROLE_ADMIN);

        // Act
        UserDomain result = mapper.toDomain(testUser);

        // Assert
        assertNotNull(result);
        assertEquals("ROLE_ADMIN", result.getRole());
    }

    @Test
    void toEntity_WithDifferentRole_ShouldMapCorrectly() {
        // Arrange
        testUserDomain.setRole("ROLE_ADMIN");

        // Act
        User result = mapper.toEntity(testUserDomain);

        // Assert
        assertNotNull(result);
        assertEquals(RoleEnum.ROLE_ADMIN, result.getRole());
    }
}
