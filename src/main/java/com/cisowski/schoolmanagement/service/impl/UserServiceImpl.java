package com.cisowski.schoolmanagement.service.impl;

import com.cisowski.schoolmanagement.exception.EntityNotFoundException;
import com.cisowski.schoolmanagement.model.entity.Authority;
import com.cisowski.schoolmanagement.model.entity.User;
import com.cisowski.schoolmanagement.repository.UsersAuthoritiesRepository;
import com.cisowski.schoolmanagement.repository.impl.UsersAuthoritiesRepositoryImpl;
import com.cisowski.schoolmanagement.utility.UserNotFoundException;
import com.cisowski.schoolmanagement.repository.UserRepository;
import com.cisowski.schoolmanagement.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UsersAuthoritiesRepositoryImpl authorityRepository;
    @Override
    public User addUser(User user, Authority[] authorities) {
        HashSet<Authority> uniqueAuthorities = new HashSet<>();
        Collections.addAll(uniqueAuthorities, authorities);

        String hashPassword = new BCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(hashPassword);

        User savedUser = userRepository.save(user);
        authorityRepository.addUserAuthorities(uniqueAuthorities, user);

        return savedUser;
    }

    @Override
    public Integer deleteUser(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty())
            throw new EntityNotFoundException(User.class, "id", userId.toString());
        userRepository.deleteById(userId);
        return userId;
    }

    @Override
    public User updateUser(User user) {
        Optional<User> existingUser = userRepository.findById(user.getId());
        if(existingUser.isEmpty())
            throw new UserNotFoundException();

        if(user.getAuthority() == null && existingUser.get().getAuthority() != null)
            user.setAuthority(existingUser.get().getAuthority());

        userRepository.save(user);
        return user;
    }

    @Override
    public User findById(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty())
            throw new EntityNotFoundException(User.class,"id", userId.toString());

        return user.orElse(null);
    }

    @Override
    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByEmail(String email){
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email));
        if(user.isEmpty())
            throw new EntityNotFoundException(User.class,"Email", email);
        return user.orElse(null);
    }

    @Override
    public User findByEmailNoEx(String email) {
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email));
        return user.orElse(null);
    }
}
