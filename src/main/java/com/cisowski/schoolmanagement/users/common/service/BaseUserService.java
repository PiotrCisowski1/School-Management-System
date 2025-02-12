package com.cisowski.schoolmanagement.users.common.service;

import com.cisowski.schoolmanagement.common.utility.PasswordGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public interface BaseUserService {

    void deleteUser(Integer userId);
    default String generateNewUserPassword(){
        return PasswordGenerator.generatePassword();
    }
    default String hashPassword(String password) { return new BCryptPasswordEncoder().encode(password); }

}
