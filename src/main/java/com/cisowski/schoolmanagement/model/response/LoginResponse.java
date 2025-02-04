package com.cisowski.schoolmanagement.model.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private long expiresIn;

}
