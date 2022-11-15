package com.tnc.userManagement.service;

import com.tnc.userManagement.service.exception.EmailExistException;
import com.tnc.userManagement.service.exception.EmailNotFoundException;
import com.tnc.userManagement.service.model.UserDomain;

import javax.mail.MessagingException;
import java.util.List;

public interface IUserService {

    void resetPassword(String email) throws MessagingException, EmailNotFoundException;

    UserDomain findByEmail(String email);

    UserDomain get(Long id);

    void deleteUser(Long id);

//    UserDomain updateUser(String currentEmail, String newFirstName, String newLastName, String newEmail, String role, boolean isActive, boolean isNotActive) throws EmailNotFoundException, EmailExistException;

//    UserDomain updateUser(String newFirstName, String newLastName, String newEmail, String role, boolean isActive, boolean isNotActive) throws EmailNotFoundException, EmailExistException;

    UserDomain updateUser(Long id, String newFirstName, String newLastName, String newEmail, String role, boolean isActive, boolean isNotActive) throws EmailNotFoundException, EmailExistException;

    List<UserDomain> getAll();

    UserDomain addNewUserWithSpecificRole(String firstName, String lastName, String email, String role, boolean parseBoolean, boolean parseBoolean1);
}
