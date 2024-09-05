package com.cisowski.schoolmanagement.service.impl;

import com.cisowski.schoolmanagement.model.entity.User;
import com.cisowski.schoolmanagement.model.entity.UserDetails;
import com.cisowski.schoolmanagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SchoolUserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new UsernameNotFoundException("User with given e-mail is not found, e-mail: '"+email);
        }

        return new UserDetails(user);
    }

}
