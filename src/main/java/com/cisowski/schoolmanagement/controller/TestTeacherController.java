package com.cisowski.schoolmanagement.controller;

import com.cisowski.schoolmanagement.service.impl.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teacher")
public class TestTeacherController {

    @GetMapping("/hello")
    public String hello(@AuthenticationPrincipal UserDetails userDetails) {

        return "Hello Student! Your data: \r\n\r\n"+userDetails.toString();
    }
}
