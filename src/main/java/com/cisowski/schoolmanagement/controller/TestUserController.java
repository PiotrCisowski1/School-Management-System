package com.cisowski.schoolmanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class TestUserController {

    @GetMapping("/hello")
    public String helloEveryone() { return "Hello user"; }
}
