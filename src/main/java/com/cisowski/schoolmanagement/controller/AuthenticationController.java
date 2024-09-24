package com.cisowski.schoolmanagement.controller;

import com.cisowski.schoolmanagement.model.entity.LoginResponse;
import com.cisowski.schoolmanagement.model.entity.LoginUserDto;
import com.cisowski.schoolmanagement.model.entity.User;
import com.cisowski.schoolmanagement.model.entity.UserDetails;
import com.cisowski.schoolmanagement.service.impl.AuthenticationService;
import com.cisowski.schoolmanagement.service.impl.JwtService;
import com.cisowski.schoolmanagement.utility.DbLogger;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final Logger logger;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.logger = DbLogger.getLogger();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto){
        String message = "Received login request, starting authentication";
        logger.info(DbLogger.buildInfoMessage(message));

        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getJwtExpirationTime());

        message = String.format("User '%s' authenticated, token expiration time is: %s", authenticatedUser.getId(), loginResponse.getExpiresIn());
        logger.info(DbLogger.buildInfoMessage(message));

        return ResponseEntity.ok(loginResponse);
    }
}
