package com.tnc.userManagement.service.ServiceImpl;

import com.tnc.resilience.util.ResilienceExecutor;
import com.tnc.userManagement.repository.UserRepository;
import com.tnc.userManagement.repository.entity.User;
import com.tnc.userManagement.service.IUserService;
import com.tnc.userManagement.service.constant.RoleEnum;
import com.tnc.userManagement.service.exception.EmailExistException;
import com.tnc.userManagement.service.exception.EmailNotFoundException;
import com.tnc.userManagement.service.mapper.UserDomainMapper;
import com.tnc.userManagement.service.model.UserDomain;
import com.tnc.userManagement.service.security.UserPrincipal;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import java.security.SecureRandom;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import static com.tnc.userManagement.service.constant.UserImplConstant.EMAIL_ALREADY_EXIST;
import static com.tnc.userManagement.service.constant.UserImplConstant.NO_USER_FOUND_BY_EMAIL;

/**
 * User service implementation with integrated circuit breaker and retry patterns.
 * Works with domain models and uses mapper for entity conversion.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("userDetailsService")
public class UserServiceImpl implements IUserService, UserDetailsService {

    private final UserRepository userRepository;
    private final UserDomainMapper userDomainMapper;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Execute a database operation with circuit breaker and retry protection.
     */
    private <R> R executeDatabase(Supplier<R> operation) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("database");
        Retry retry = retryRegistry.retry("database");
        Supplier<R> decoratedSupplier = ResilienceExecutor.decorateSupplier(circuitBreaker, retry, operation);
        return decoratedSupplier.get();
    }

    @Override
    public UserDomain addNewUserWithSpecificRole(String firstName, String lastName, String email, String role, boolean isActive, boolean isNotActive) {
        log.info("Adding new user with email: {}", email);
        
        var userDomain = new UserDomain();
        userDomain.setUserId(generateUserId());
        String password = generatePassword();
        userDomain.setFirstName(firstName);
        userDomain.setLastName(lastName);
        userDomain.setEmail(email);
        userDomain.setPassword(passwordEncoder.encode(password));
        userDomain.setRole(getRoleEnumName(role).name());
        userDomain.setAuthorities(getRoleEnumName(role).getAuthorities());
        userDomain.setActive(isActive);
        userDomain.setNotLocked(isNotActive);
        userDomain.setJoinDate(new Date());
        
        User savedUser = executeDatabase(() -> 
            userRepository.save(userDomainMapper.toEntity(userDomain))
        );
        
        log.info("User created with ID: {}, password: {}", savedUser.getId(), password);
        return userDomainMapper.toDomain(savedUser);
    }

    public UserDomain addNewUserWithPassword(String firstName, String lastName, String email, String password, String role, boolean isActive, boolean isNotActive) {
        log.info("Adding new user with password for email: {}", email);
        
        var userDomain = new UserDomain();
        userDomain.setUserId(generateUserId());
        userDomain.setFirstName(firstName);
        userDomain.setLastName(lastName);
        userDomain.setEmail(email);
        userDomain.setPassword(passwordEncoder.encode(password));
        userDomain.setRole(getRoleEnumName(role).name());
        userDomain.setAuthorities(getRoleEnumName(role).getAuthorities());
        userDomain.setActive(isActive);
        userDomain.setNotLocked(isNotActive);
        userDomain.setJoinDate(new Date());
        
        User savedUser = executeDatabase(() -> 
            userRepository.save(userDomainMapper.toEntity(userDomain))
        );
        
        log.info("User created with ID: {}", savedUser.getId());
        return userDomainMapper.toDomain(savedUser);
    }

    @Override
    public UserDomain updateUser(Long id, String newFirstName, String newLastName, String newEmail, String role, boolean isActive, boolean isNotActive) throws EmailNotFoundException, EmailExistException {
        log.info("Updating user with ID: {}", id);
        
        // Find the existing user and convert to domain
        UserDomain userDomain = executeDatabase(() -> 
            userRepository.findById(id)
                .map(userDomainMapper::toDomain)
                .orElse(null)
        );
        
        if (userDomain == null) {
            throw new EmailNotFoundException("User not found with id: " + id);
        }
        
        // Check if the email is being changed and if the new email already exists for a different user
        if (!userDomain.getEmail().equals(newEmail)) {
            UserDomain userWithEmail = findByEmail(newEmail);
            if (userWithEmail != null && !userWithEmail.getId().equals(id)) {
                throw new EmailExistException("Email already exists: " + newEmail);
            }
        }
        
        // Update the user fields
        userDomain.setFirstName(newFirstName);
        userDomain.setLastName(newLastName);
        userDomain.setEmail(newEmail);
        userDomain.setRole(getRoleEnumName(role).name());
        userDomain.setAuthorities(getRoleEnumName(role).getAuthorities());
        userDomain.setActive(isActive);
        userDomain.setNotLocked(isNotActive);
        
        // Save the updated user and return the domain
        User savedUser = executeDatabase(() -> 
            userRepository.save(userDomainMapper.toEntity(userDomain))
        );
        
        log.info("User updated: {}", id);
        return userDomainMapper.toDomain(savedUser);
    }

    @Override
    public List<UserDomain> getAll() {
        log.debug("Getting all users");
        return executeDatabase(() -> 
            userDomainMapper.toDomainList(userRepository.findAll())
        );
    }

    @Override
    public UserDomain get(Long id) {
        log.debug("Getting user by ID: {}", id);
        return executeDatabase(() -> 
            userRepository.findById(id)
                .map(userDomainMapper::toDomain)
                .orElse(null)
        );
    }

    @Override
    public UserDomain findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return executeDatabase(() -> {
            User user = userRepository.findUserByEmail(email);
            return user != null ? userDomainMapper.toDomain(user) : null;
        });
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        executeDatabase(() -> {
            userRepository.deleteById(id);
            log.info("User deleted: {}", id);
            return null;
        });
    }

    @Override
    public void resetPassword(String email) throws MessagingException, EmailNotFoundException {
        log.info("Resetting password for email: {}", email);
        
        UserDomain userDomain = findByEmail(email);
        if (userDomain == null) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        }
        
        String password = generatePassword();
        userDomain.setPassword(passwordEncoder.encode(password));
        
        executeDatabase(() -> 
            userRepository.save(userDomainMapper.toEntity(userDomain))
        );
        
        emailService.sendNewPasswordEmail(userDomain.getFirstName(), password, userDomain.getEmail());
        log.info("Password reset for user: {}", email);
    }

    public UserDomain validateNewUsernameAndEmail(String currentEmail, String newEmail) throws EmailNotFoundException, EmailExistException {
        UserDomain userByEmail = findByEmail(newEmail);
        if (StringUtils.isNotBlank(currentEmail)) {
            UserDomain notBlankEmail = findByEmail(currentEmail);
            if (notBlankEmail == null) {
                throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + currentEmail);
            }
            if (userByEmail != null && !notBlankEmail.getId().equals(userByEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXIST + currentEmail);
            }
            return notBlankEmail;
        } else {
            if (userByEmail != null) {
                throw new EmailExistException(String.valueOf(EMAIL_ALREADY_EXIST));
            }
            return null;
        }
    }

    private String generatePassword() {
        return RandomStringUtils.random(10, 0, 0, true, true, null, new SecureRandom());
    }

    private String generateUserId() {
        return RandomStringUtils.random(10, 0, 0, false, true, null, new SecureRandom());
    }

    private RoleEnum getRoleEnumName(String role) {
        try {
            return RoleEnum.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return RoleEnum.ROLE_USER; // Default to ROLE_USER if invalid
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDomain user = findByEmail(email);
        if (user == null) {
            log.error("No user found by email: {}", email);
            throw new UsernameNotFoundException("No user found by email: " + email);
        } else {
            log.info("Found user by email: {}", email);
            return new UserPrincipal(user);
        }
    }
}
