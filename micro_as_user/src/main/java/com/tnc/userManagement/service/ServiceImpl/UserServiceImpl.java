package com.tnc.userManagement.service.ServiceImpl;

import com.tnc.userManagement.repository.UserRepository;
import com.tnc.userManagement.service.IUserService;
import com.tnc.userManagement.service.constant.RoleEnum;
import com.tnc.userManagement.service.exception.EmailExistException;
import com.tnc.userManagement.service.exception.EmailNotFoundException;
import com.tnc.userManagement.service.mapper.UserDomainMapper;
import com.tnc.userManagement.service.model.UserDomain;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.List;

import static com.tnc.userManagement.service.constant.RoleEnum.ROLE_USER;
import static com.tnc.userManagement.service.constant.UserImplConstant.EMAIL_ALREADY_EXIST;
import static com.tnc.userManagement.service.constant.UserImplConstant.NO_USER_FOUND_BY_EMAIL;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@RequiredArgsConstructor
@Qualifier("userDetailsService")
public class UserServiceImpl implements IUserService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass()); //getClass = this class

    private final UserRepository userRepository;
    private final UserDomainMapper userDomainMapper;

    @Autowired
    private final EmailService emailService;

    @Override
    public UserDomain addNewUserWithSpecificRole(String firstName, String lastName, String email, String role, boolean isActive, boolean isNotActive) {
        var userDomain = new UserDomain();
        userDomain.setUserId(generateUserId());
        String password = generatePassword();
        userDomain.setFirstName(firstName);
        userDomain.setLastName(lastName);
        userDomain.setEmail(email);
        userDomain.setRole(getRoleEnumName(role).name());
        userDomain.setAuthorities(getRoleEnumName(role).getAuthorities());
        userDomain.setActive(isActive);
        userDomain.setNotLocked(isNotActive);
        userDomain.setJoinDate(new Date());
        userRepository.save(userDomainMapper.toEntity(userDomain));
        LOGGER.info("Password is " + password);
        return userDomain;
    }

    //    @Override
//    public UserDomain updateUser(Long id, String newFirstName, String newLastName, String newEmail, String role, boolean isActive, boolean isNotActive) throws EmailNotFoundException, EmailExistException {
//        var userDomain = new UserDomain();
////        var userDomain = validateNewUsernameAndEmail(EMPTY, newEmail);
//        userDomain.setId(id);
//        userDomain.setFirstName(newFirstName);
//        userDomain.setLastName(newLastName);
//        userDomain.setEmail(newEmail);
//        userDomain.setRole(getRoleEnumName(role).name());
//        userDomain.setAuthorities(getRoleEnumName(role).getAuthorities());
//        userDomain.setActive(isActive);
//        userDomain.setNotLocked(isNotActive);
//        userDomain.setJoinDate(new Date());
//        userRepository.save(userDomainMapper.toEntity(userDomain));
//        return userDomain;
//    }
    @Override
    public UserDomain updateUser(Long id, String newFirstName, String newLastName, String newEmail, String role, boolean isActive, boolean isNotActive) throws EmailNotFoundException, EmailExistException {
        var userDomain = addNewUserWithSpecificRole(newFirstName, newLastName, newEmail, role, isActive, isNotActive);
        userDomain.setId(id);
        userDomain.setFirstName(newFirstName);
        userDomain.setLastName(newLastName);
        userDomain.setEmail(newEmail);
        userDomain.setRole(getRoleEnumName(role).name());
        userDomain.setAuthorities(getRoleEnumName(role).getAuthorities());
        userDomain.setActive(isActive);
        userDomain.setNotLocked(isNotActive);
        userDomain.setJoinDate(new Date());
        userRepository.save(userDomainMapper.toEntity(userDomain));
        return userDomain;
    }

    @Override
    public List<UserDomain> getAll() {
        return userDomainMapper.toDomainList(userRepository.findAll());
    }

    @Override
    public UserDomain get(Long id) {
        return userDomainMapper.toDomain(userRepository.getById(id));
    }

    @Override
    public UserDomain findByEmail(String email) {
        return userDomainMapper.toDomain(userRepository.findUserByEmail(email));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void resetPassword(String email) throws MessagingException, EmailNotFoundException {
        var userDomain = userDomainMapper.toDomain(userRepository.findUserByEmail(email));
        if (userDomain == null) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        }
        String password = generatePassword();
        userRepository.save(userDomainMapper.toEntity(userDomain));
        emailService.sendNewPasswordEmail(userDomain.getFirstName(), password, userDomain.getEmail());
    }

//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        var userEmail = userDomainMapper.toDomain(userRepository.findUserByEmail(email));
//        if (userEmail == null) {
//            LOGGER.error(NO_USER_FOUND_BY_USERNAME + email);
//            throw new UsernameNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
//        } else {
//            validateLoginAttempt(userEmail);
//            userEmail.setLastLoginDateDisplay(userEmail.getLastLoginDateDisplay());
//            userEmail.setLastLoginDate(new Date());
//            userRepository.save(userDomainMapper.toEntity(userEmail));
//            UserPrincipal userPrincipal = new UserPrincipal(userEmail);
//            LOGGER.info(FOUND_USER_BY_USERNAME + email);
//            return userPrincipal;
//        }
//    }

    public UserDomain validateNewUsernameAndEmail(String currentEmail, String newEmail) throws EmailNotFoundException, EmailExistException {
        var userByEmail = findByEmail(newEmail);
        if (StringUtils.isNotBlank(currentEmail)) {
            var notBlankEmail = findByEmail(currentEmail);
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
        return RandomStringUtils.randomAlphanumeric(10);

    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private RoleEnum getRoleEnumName(String role) {
        return RoleEnum.valueOf(role.toUpperCase());
    }
}
