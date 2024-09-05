package com.cisowski.schoolmanagement.service;

import com.cisowski.schoolmanagement.model.entity.Authority;
import com.cisowski.schoolmanagement.model.entity.User;
import com.cisowski.schoolmanagement.utility.UserNotFoundException;

import java.util.Collection;
import java.util.HashSet;

public interface UserService {
    User addUser(User user, Authority[] authorities);
    Integer deleteUser(Integer userId);
    User updateUser(User user);
    User findById(Integer userId);
    Collection<User> findAll();
    User findByEmail(String email);
}
