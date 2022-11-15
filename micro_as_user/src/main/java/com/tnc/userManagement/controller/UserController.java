package com.tnc.userManagement.controller;

import com.tnc.userManagement.controller.dto.UserDTO;
import com.tnc.userManagement.controller.dtoMapper.UserDTOMapper;
import com.tnc.userManagement.service.IUserService;
import com.tnc.userManagement.service.exception.EmailExistException;
import com.tnc.userManagement.service.exception.EmailNotFoundException;
import com.tnc.userManagement.service.exception.UserNotFoundException;
import com.tnc.userManagement.service.exception.UsernameExistException;
import com.tnc.userManagement.service.model.HttpResponse;
import com.tnc.userManagement.service.validation.OnCreate;
import com.tnc.userManagement.service.validation.OnUpdate;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
@RequestMapping(path = {"/users"})
//init
public class UserController {
    public static final String EMAIL_SENT = "An email with a new password was sent to: ";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully. ";
    private final IUserService userService;
    private final UserDTOMapper userDTOMapper;

    @PostMapping("/add")
    @Validated(OnCreate.class)
    public ResponseEntity<UserDTO> addNewUser(@RequestBody UserDTO userDTO) {
        var newUser = userDTOMapper.toDTO(userService.addNewUserWithSpecificRole(userDTO.firstName(), userDTO.lastName(), userDTO.email(), userDTO.role(),
                Boolean.parseBoolean(String.valueOf(userDTO.isNotLocked())), Boolean.parseBoolean(String.valueOf(userDTO.isActive()))));
        return new ResponseEntity<>(newUser, OK);
    }

    @PutMapping("/update")
    @Validated(OnUpdate.class)
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, EmailNotFoundException {
        var updateUser = userService.updateUser(userDTO.id(), userDTO.firstName(), userDTO.lastName(), userDTO.email(), userDTO.role(),
                Boolean.parseBoolean(String.valueOf(userDTO.isNotLocked())), Boolean.parseBoolean(String.valueOf(userDTO.isActive())));
        return new ResponseEntity<>(userDTOMapper.toDTO(updateUser), OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("username") String username) {
        var userDomain = userService.findByEmail(username);
        return new ResponseEntity<>(userDTOMapper.toDTO(userDomain), OK);
    }

    @GetMapping()
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userDTOMapper.toDTOList(userService.getAll());
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException, MessagingException {
        userService.resetPassword(email);
        return response(OK, EMAIL_SENT + email);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return response(NO_CONTENT, USER_DELETED_SUCCESSFULLY);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }
}
