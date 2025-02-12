package com.cisowski.schoolmanagement.users.common.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginUserRequest {
    @NotNull(message = "Email cannot be empty")
    private String email;
    @NotNull(message = "Password cannot be empty")
    private String password;

}
