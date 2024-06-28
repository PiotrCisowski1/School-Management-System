package com.cisowski.schoolmanagement.controller;

import com.cisowski.schoolmanagement.service.impl.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class TestAdminController {

    @GetMapping("/hello")
    public String hello(@AuthenticationPrincipal UserDetails userDetails) {

        String data = String.format("First name: %s, last name: %s, authorities: %s",userDetails.getFirstName(), userDetails.getLastName(), userDetails.getAuthorities().toArray()[0]);
        return "Hello Student! Your data: \r\n\r\n"+data+"User: "+userDetails.getFirstName()+" email: "+ userDetails.getEmail();
    }
}
