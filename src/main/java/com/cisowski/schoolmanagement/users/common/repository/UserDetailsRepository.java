package com.cisowski.schoolmanagement.users.common.repository;

import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDetailsRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);
}
