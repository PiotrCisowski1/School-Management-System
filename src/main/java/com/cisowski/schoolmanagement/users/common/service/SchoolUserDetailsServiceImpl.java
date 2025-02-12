package com.cisowski.schoolmanagement.users.common.service;

import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.common.model.UserDetailsEntity;
import com.cisowski.schoolmanagement.users.common.repository.UserDetailsRepository;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
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

        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            String errorMessage = String.format("No user was found with given email: %s", email);
            DbLogger.error(errorMessage);
            throw new UsernameNotFoundException("User with given e-mail is not found, e-mail: '" + email);
        }

        message = String.format("User found, returning user details: %s", user.toString());
        DbLogger.info(message);
        return new UserDetailsEntity(user.get());
    }

}