package com.cisowski.schoolmanagement.users.teacher.repository;

import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.common.repository.BaseUserRepository;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;

import java.util.List;

public interface TeacherRepository extends BaseUserRepository<TeacherEntity, Integer> {
    boolean existsByTeachingSubjects(SubjectEntity subject);
    List<TeacherEntity> findByTeachingSubjects(SubjectEntity subject);
}
