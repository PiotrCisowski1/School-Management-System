package com.cisowski.schoolmanagement.controller;

import com.cisowski.schoolmanagement.exception.EntityNotFoundException;
import com.cisowski.schoolmanagement.model.entity.Authority;
import com.cisowski.schoolmanagement.model.entity.User;
import com.cisowski.schoolmanagement.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @PostMapping
    public ResponseEntity<User> addUser(@RequestPart User user, @RequestPart Authority[] authorities) {
        Optional<User> duplicateUser = Optional.ofNullable(userService.findByEmail(user.getEmail()));
        if(duplicateUser.isPresent())
            return new ResponseEntity(duplicateUser, HttpStatus.CONFLICT);
        userService.addUser(user, authorities);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> deleteUser(@PathVariable Integer userId) throws EntityNotFoundException {
        userService.deleteUser(userId);
        return new ResponseEntity<>(userId, HttpStatus.OK);
    }
    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user){
        userService.updateUser(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> findUserById(@PathVariable Integer userId) throws EntityNotFoundException {
        User user = userService.findById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<Collection<User>> getAllUsers() throws EntityNotFoundException {
        Collection<User> users = userService.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}
