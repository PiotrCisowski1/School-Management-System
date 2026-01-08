package com.cisowski.schoolmanagement.common.security.authentication;

import com.cisowski.schoolmanagement.common.exception.type.AccessDeniedException;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.users.common.model.*;
import com.cisowski.schoolmanagement.users.common.repository.UserDetailsRepository;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserDetailsRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

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

    public LoginResponse refreshUsersToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsEntity userDetails = (UserDetailsEntity) auth.getPrincipal();
        if(userDetails == null || userDetails.getUser() == null)
            throw new AccessDeniedException("You have no rights to use this resource");

        DbLogger.info("Received refresh token request for User with ID: " + userDetails.getId());
        String token = jwtService.generateToken(userDetails.getUser());
        long expiresIn = jwtService.getJwtExpirationTime();

        return new LoginResponse(token, expiresIn);
    }

    public UserSummaryResponse getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsEntity userDetails = (UserDetailsEntity) auth.getPrincipal();
        UserSummaryResponse response = new UserSummaryResponse();
        if(userDetails == null || userDetails.getUser() == null)
            return response;

        UserEntity user = userDetails.getUser();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setBirthDate(user.getBirthDate());
        response.setGender(user.getGender());
        response.setPhoneNumber(user.getPhoneNumber());
        return response;
    }
}
