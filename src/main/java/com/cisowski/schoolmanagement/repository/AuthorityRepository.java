package com.cisowski.schoolmanagement.repository;

import com.cisowski.schoolmanagement.model.entity.Authority;
import com.cisowski.schoolmanagement.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
//    Collection<Authority> addUserAuthorities(Set<Authority> authorities, User user);
//    void deleteAuthoritiesForUser(User user);
//    HashSet<Authority> findAuthoritiesForUser(User user);
}
