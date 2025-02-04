package com.cisowski.schoolmanagement.service;

import com.cisowski.schoolmanagement.model.entity.Authority;
import com.cisowski.schoolmanagement.model.entity.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface AuthorityService {

    Collection<Authority> addUserAuthorities(Set<Authority> authorities, User user);
    void deleteUserAuthorities(User user);

}
