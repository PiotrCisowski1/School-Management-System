package com.cisowski.schoolmanagement.common.security.authentication;

import com.cisowski.schoolmanagement.users.common.model.*;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping()
@RestController
public class AuthenticationController {

    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserRequest loginUserDto){
        String message = "Received login request, starting authentication";
        DbLogger.info(message);

        UserEntity authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getJwtExpirationTime());

        message = String.format("User with ID: %s authenticated, token expiration time is: %s", authenticatedUser.getId(), loginResponse.getExpiresIn());
        DbLogger.info(message);

        return ResponseEntity.ok(loginResponse);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/token/refresh")
    public ResponseEntity<LoginResponse> refreshToken() {
        LoginResponse response = authenticationService.refreshUsersToken();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserSummaryResponse> getAuthenticatedUserId() {
        UserSummaryResponse userId = authenticationService.getAuthenticatedUserId();
        return ResponseEntity.ok(userId);
    }
}
