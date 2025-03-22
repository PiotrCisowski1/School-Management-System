package com.cisowski.schoolmanagement.users.student.repository;

import com.cisowski.schoolmanagement.users.common.repository.BaseUserRepository;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;

public interface StudentRepository extends BaseUserRepository<StudentEntity, Integer> {
boolean existsByYearbook(YearbookEntity yearbook);
}
