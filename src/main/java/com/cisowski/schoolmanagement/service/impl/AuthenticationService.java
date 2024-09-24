package com.cisowski.schoolmanagement.service.impl;

import com.cisowski.schoolmanagement.model.entity.LoginUserDto;
import com.cisowski.schoolmanagement.model.entity.User;
import com.cisowski.schoolmanagement.repository.UserRepository;
import com.cisowski.schoolmanagement.utility.DbLogger;
import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    Logger logger = DbLogger.getLogger();

    public AuthenticationService(UserRepository userRepository,  AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    public User authenticate(LoginUserDto input){
        String message = "Authenticating User with email and password";
        logger.info(DbLogger.buildInfoMessage(message));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                input.getEmail(), input.getPassword()
        ));
        return userRepository.findByEmail(input.getEmail());
    }
}
