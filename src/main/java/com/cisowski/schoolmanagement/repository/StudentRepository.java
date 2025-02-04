package com.cisowski.schoolmanagement.repository;

import com.cisowski.schoolmanagement.model.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends BaseUserRepository<Student, Integer> {
}
