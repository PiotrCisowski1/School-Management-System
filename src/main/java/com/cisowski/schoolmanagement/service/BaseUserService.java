package com.cisowski.schoolmanagement.service;

import com.cisowski.schoolmanagement.utility.PasswordGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public interface BaseUserService {

    void deleteUser(Integer userId);
    default String generateNewUserPassword(){
        return PasswordGenerator.generatePassword();
    }
    default String hashPassword(String password) { return new BCryptPasswordEncoder().encode(password); }

}
