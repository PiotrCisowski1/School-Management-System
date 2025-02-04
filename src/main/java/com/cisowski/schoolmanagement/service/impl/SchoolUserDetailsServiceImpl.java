package com.cisowski.schoolmanagement.service.impl;

import com.cisowski.schoolmanagement.model.entity.User;
import com.cisowski.schoolmanagement.model.entity.UserDetails;
import com.cisowski.schoolmanagement.repository.BaseUserRepository;
import com.cisowski.schoolmanagement.repository.UserDetailsRepository;
import com.cisowski.schoolmanagement.utility.DbLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SchoolUserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserDetailsRepository userRepository;

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String message = String.format("Searching for user details with given email: %s", email);
        DbLogger.info(message);

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            String errorMessage = String.format("No user was found with given email: %s", email);
            DbLogger.error(errorMessage);
            throw new UsernameNotFoundException("User with given e-mail is not found, e-mail: '" + email);
        }

        message = String.format("User found, returning user details: %s", user.toString());
        DbLogger.info(message);
        return new UserDetails(user.get());
    }

}