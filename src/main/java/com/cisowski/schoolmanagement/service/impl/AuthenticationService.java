package com.cisowski.schoolmanagement.service.impl;

import com.cisowski.schoolmanagement.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.model.request.LoginUserRequest;
import com.cisowski.schoolmanagement.model.entity.User;
import com.cisowski.schoolmanagement.repository.BaseUserRepository;
import com.cisowski.schoolmanagement.repository.UserDetailsRepository;
import com.cisowski.schoolmanagement.utility.DbLogger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserDetailsRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserDetailsRepository userRepository, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    public User authenticate(LoginUserRequest input){
        String message = "Authenticating User with email and password";
        DbLogger.info(message);

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                input.getEmail(), input.getPassword()
        ));
        Optional<User> user = userRepository.findByEmail(input.getEmail());
        if(user.isEmpty())
            throw new EntityNotFoundException(User.class, "Email", input.getEmail());
        return user.get();
    }
}
