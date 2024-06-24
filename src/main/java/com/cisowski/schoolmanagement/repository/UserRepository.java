package com.cisowski.schoolmanagement.repository;

import com.cisowski.schoolmanagement.model.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByEmail(String email) throws UsernameNotFoundException;
}
