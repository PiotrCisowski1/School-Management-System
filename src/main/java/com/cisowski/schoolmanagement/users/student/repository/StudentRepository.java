package com.cisowski.schoolmanagement.users.student.repository;

import com.cisowski.schoolmanagement.users.common.repository.BaseUserRepository;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends BaseUserRepository<StudentEntity, Integer> {
    boolean existsByYearbook(YearbookEntity yearbook);

    Optional<StudentEntity> findByIdAndIsHideFalse(Integer studentId);

    List<StudentEntity> findAllByIdInAndIsHideFalse(List<Integer> ids);

    List<StudentEntity> findAllByIsHideFalse();
}
