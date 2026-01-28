package com.cisowski.schoolmanagement.common.security.authentication;

import com.cisowski.schoolmanagement.common.annotation.SecurityResponses;
import com.cisowski.schoolmanagement.users.common.model.*;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping()
@RestController
@Tag(name = "Authentication", description = "Secure entry point for users. Handles identity verification and issues JWT Bearer tokens.")
public class AuthenticationController {

    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login to the system",
            description = "Authenticates user with given credentials and generates token to authorize across the system.")
    @ApiResponse(responseCode = "404", description = "User with given email not found")
    @ApiResponse(responseCode = "200", description = "User authenticated successfully")
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
    @Operation(
            summary = "Refresh token",
            description = "Generate new token to authorize across the system. Requires already authenticated user.")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @ApiResponse(responseCode = "200", description = "Returns newly generated token")
    public ResponseEntity<LoginResponse> refreshToken() {
        LoginResponse response = authenticationService.refreshUsersToken();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    @SecurityResponses
    @Operation(
            summary = "Get user's information",
            description = "Retrieves actually authenticated user's data. Requires already authenticated user.")
    @ApiResponse(responseCode = "200", description = "Returns user's data")
    public ResponseEntity<UserSummaryResponse> getAuthenticatedUserId() {
        UserSummaryResponse userId = authenticationService.getAuthenticatedUserId();
        return ResponseEntity.ok(userId);
    }
}
