package com.cisowski.schoolmanagement.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class TestPublicController {

    @GetMapping("/hello")
    public String hello(){
        return "Hello everyone";
    }
}
