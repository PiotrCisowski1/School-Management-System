package com.cisowski.schoolmanagement.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class TestAdminController {

   // @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/hello")
    public String hello(){
        return "Hello admin";
    }
}
