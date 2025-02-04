package com.cisowski.schoolmanagement.repository;

import com.cisowski.schoolmanagement.model.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParentRepository extends BaseUserRepository<Parent, Integer> {
}
