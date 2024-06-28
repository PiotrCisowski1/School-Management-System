package com.cisowski.schoolmanagement.controller;

import com.cisowski.schoolmanagement.service.impl.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
public class TestStudentController {

    @GetMapping("/hello")
    public String helloEveryone(@AuthenticationPrincipal UserDetails userDetails) {

        return "Hello Student! Your data: \r\n\r\n"+userDetails.toString();
    }
}
