package com.cisowski.schoolmanagement.users.common.model;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private long expiresIn;

}
