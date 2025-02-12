package com.cisowski.schoolmanagement.users.common;

import com.cisowski.schoolmanagement.users.common.model.LoginResponse;
import com.cisowski.schoolmanagement.users.common.service.AuthenticationService;
import com.cisowski.schoolmanagement.users.common.service.JwtService;
import com.cisowski.schoolmanagement.users.common.model.LoginUserRequest;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
