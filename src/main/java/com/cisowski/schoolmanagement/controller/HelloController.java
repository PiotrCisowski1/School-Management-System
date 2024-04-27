package com.cisowski.schoolmanagement.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class HelloController {

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/adminHello")
    public String hello(){
        return "Hello admin";
    }
    @GetMapping("/publicHello")
    public String helloEveryone() { return "Hello everyone"; }
}
