package com.cisowski.schoolmanagement.users.common.service;

import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.users.common.model.LoginUserRequest;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.common.repository.UserDetailsRepository;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
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

    public UserEntity authenticate(LoginUserRequest input){
        String message = "Authenticating User with email and password";
        DbLogger.info(message);

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                input.getEmail(), input.getPassword()
        ));
        Optional<UserEntity> user = userRepository.findByEmail(input.getEmail());
        if(user.isEmpty())
            throw new EntityNotFoundException(UserEntity.class, "Email", input.getEmail());
        return user.get();
    }
}
