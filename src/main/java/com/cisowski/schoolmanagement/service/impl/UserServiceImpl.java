package com.cisowski.schoolmanagement.service.impl;

import com.cisowski.schoolmanagement.exception.type.EmailAlreadyExistsException;
import com.cisowski.schoolmanagement.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.model.entity.Authority;
import com.cisowski.schoolmanagement.model.entity.User;
import com.cisowski.schoolmanagement.repository.impl.UsersAuthoritiesRepositoryImpl;
import com.cisowski.schoolmanagement.repository.UserRepository;
import com.cisowski.schoolmanagement.service.UserService;
import com.cisowski.schoolmanagement.utility.DbLogger;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UsersAuthoritiesRepositoryImpl authorityRepository;
    Logger logger = DbLogger.getLogger();

    @Override
    @Transactional
    public User addUser(User user, Authority[] authorities) {
        String message = "Starting add User function for: " + user.toString();
        logger.info(DbLogger.buildInfoMessage(message));

        HashSet<Authority> uniqueAuthorities = new HashSet<>();
        Collections.addAll(uniqueAuthorities, authorities);

        String hashPassword = new BCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(hashPassword);

        User savedUser = userRepository.save(user);
        authorityRepository.addUserAuthorities(uniqueAuthorities, savedUser);

        message = "User saved successfully, user data: " + savedUser.toString();
        logger.info(DbLogger.buildInfoMessage(message));

        return savedUser;
    }

    @Override
    public Integer deleteUser(Integer userId) {
        String message = "Starting delete User function for UserID: " + userId.toString();
        logger.info(DbLogger.buildInfoMessage(message));

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            String errorMessage = "User with given ID was not found, ID: " + userId.toString();
            logger.error(errorMessage);
            throw new EntityNotFoundException(User.class, "id", userId.toString());
        }
        userRepository.deleteById(userId);
        message = String.format("User with ID: %s, was deleted successfully", userId.toString());
        logger.info(DbLogger.buildInfoMessage(message));
        return userId;
    }

    @Override
    public User updateUser(User user) throws EmailAlreadyExistsException {
        String message = "Starting update User function for User: " + user.toString();
        logger.info(DbLogger.buildInfoMessage(message));

        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isEmpty()) {
            String errorMessage = "User with given ID was not found, ID: " + user.getId().toString();
            logger.error(errorMessage);
            throw new EntityNotFoundException(User.class, "ID", user.getId().toString());
        }

        Optional<User> sameEmailUser = Optional.ofNullable(userRepository.findByEmail(user.getEmail()));
        if (sameEmailUser.isPresent() && !Objects.equals(sameEmailUser.get().getId(), user.getId())) {
            String errorMessage = String.format("Email already exists for another User: %s, ID: %s ", user.getEmail(), sameEmailUser.get().getId());
            logger.error(errorMessage);
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        if (user.getAuthority() == null && existingUser.get().getAuthority() != null)
            user.setAuthority(existingUser.get().getAuthority());
        user.setPassword(existingUser.get().getPassword());
        userRepository.save(user);

        message = "User updated successfully: " + user.toString();
        logger.info(DbLogger.buildInfoMessage(message));
        return user;
    }

    @Override
    public User findById(Integer userId) {
        String message = "Starting find User function for UserID: " + userId.toString();
        logger.info(DbLogger.buildInfoMessage(message));

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            String errorMessage = String.format("User with ID: %s, was not found", userId.toString());
            logger.error(errorMessage);
            throw new EntityNotFoundException(User.class, "id", userId.toString());
        }

        message = "Found User with given ID: " + user.get().toString();
        logger.info(DbLogger.buildInfoMessage(message));
        return user.orElse(null);
    }

    @Override
    public Collection<User> findAll() {
        String message = "Starting find ALL Users function";
        logger.info(DbLogger.buildInfoMessage(message));

        return userRepository.findAll();
    }

    @Override
    public User findByEmail(String email) {
        String message = "Searching for User with corresponding email: "+email;
        logger.info(DbLogger.buildInfoMessage(message));

        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email));
        if (user.isEmpty()) {
            String errorMessage = String.format("User with email: %s, was not found", email);
            logger.error(errorMessage);
            throw new EntityNotFoundException(User.class, "Email", email);
        }
        return user.orElse(null);
    }

    @Override
    public User findByEmailNoEx(String email) {
        String message = "Checking if user already exists for email: " + email;
        logger.info(DbLogger.buildInfoMessage(message));

        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email));
        return user.orElse(null);
    }
}
