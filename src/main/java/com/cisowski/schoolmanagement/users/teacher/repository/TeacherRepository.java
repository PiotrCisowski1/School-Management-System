package com.cisowski.schoolmanagement.users.teacher.repository;

import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.common.repository.BaseUserRepository;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;

public interface TeacherRepository extends BaseUserRepository<TeacherEntity, Integer> {
    boolean existsByTeachingSubjects(SubjectEntity subject);
}
