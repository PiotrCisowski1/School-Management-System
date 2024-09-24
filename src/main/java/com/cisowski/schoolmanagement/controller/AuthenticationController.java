package com.cisowski.schoolmanagement.controller;

import com.cisowski.schoolmanagement.model.entity.LoginResponse;
import com.cisowski.schoolmanagement.model.entity.LoginUserDto;
import com.cisowski.schoolmanagement.model.entity.User;
import com.cisowski.schoolmanagement.model.entity.UserDetails;
import com.cisowski.schoolmanagement.service.impl.AuthenticationService;
import com.cisowski.schoolmanagement.service.impl.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping()
@RestController
public class AuthenticationController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto){
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getJwtExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }
}
