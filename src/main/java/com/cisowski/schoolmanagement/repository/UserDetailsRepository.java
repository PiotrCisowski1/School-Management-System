package com.cisowski.schoolmanagement.repository;

import com.cisowski.schoolmanagement.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDetailsRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
