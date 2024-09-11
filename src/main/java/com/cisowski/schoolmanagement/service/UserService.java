package com.cisowski.schoolmanagement.service;

import com.cisowski.schoolmanagement.model.entity.Authority;
import com.cisowski.schoolmanagement.model.entity.User;

import java.util.Collection;

public interface UserService {
    User addUser(User user, Authority[] authorities);
    Integer deleteUser(Integer userId);
    User updateUser(User user);
    User findById(Integer userId);
    Collection<User> findAll();
    User findByEmail(String email);
    User findByEmailNoEx(String email);
}
