package com.cisowski.schoolmanagement.controller;

import com.cisowski.schoolmanagement.exception.type.EmailAlreadyExistsException;
import com.cisowski.schoolmanagement.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.model.entity.Authority;
import com.cisowski.schoolmanagement.model.entity.User;
import com.cisowski.schoolmanagement.service.impl.UserServiceImpl;
import com.cisowski.schoolmanagement.utility.DbLogger;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserServiceImpl userService;
    Logger logger = DbLogger.getLogger();

    @PostMapping
    public ResponseEntity<User> addUser(@RequestPart User user, @RequestPart Authority[] authorities)
            throws EmailAlreadyExistsException {
        String message = "Received User POST request, object data: " + user.toString();
        logger.info(DbLogger.buildInfoMessage(message));

        Optional<User> duplicateUser = Optional.ofNullable(userService.findByEmailNoEx(user.getEmail()));
        if (duplicateUser.isPresent()) {
            String errorMessage = "User already exists, email: " + user.getEmail();
            logger.error(errorMessage);
            throw new EmailAlreadyExistsException(duplicateUser.get().getEmail());
        }

        userService.addUser(user, authorities);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Integer> deleteUser(@PathVariable Integer userId) throws EntityNotFoundException {
        String message = "Received User DELETE request for UserID: " + userId.toString();
        logger.info(DbLogger.buildInfoMessage(message));

        userService.deleteUser(userId);
        return new ResponseEntity<>(userId, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) throws EmailAlreadyExistsException {
        String message = "Received User PUT request for User: " + user.toString();
        logger.info(DbLogger.buildInfoMessage(message));

        userService.updateUser(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> findUserById(@PathVariable Integer userId) throws EntityNotFoundException {
        String message = "Received User GET request for UserID: " + userId.toString();
        logger.info(DbLogger.buildInfoMessage(message));

        User user = userService.findById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<User>> getAllUsers() throws EntityNotFoundException {
        String message = "Received User GET ALL request";
        logger.info(DbLogger.buildInfoMessage(message));

        Collection<User> users = userService.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}
