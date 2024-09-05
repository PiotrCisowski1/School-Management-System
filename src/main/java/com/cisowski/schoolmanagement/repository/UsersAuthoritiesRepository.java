package com.cisowski.schoolmanagement.repository;

import com.cisowski.schoolmanagement.model.entity.Authority;
import com.cisowski.schoolmanagement.model.entity.User;

import java.util.HashSet;

public interface UsersAuthoritiesRepository {
    void addUserAuthorities(HashSet<Authority> authorities, User user);
}
