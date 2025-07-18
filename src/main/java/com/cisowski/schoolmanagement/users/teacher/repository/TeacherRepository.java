package com.cisowski.schoolmanagement.users.teacher.repository;

import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.common.repository.BaseUserRepository;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends BaseUserRepository<TeacherEntity, Integer> {
    boolean existsByTeachingSubjects(SubjectEntity subject);
    List<TeacherEntity> findByTeachingSubjects(SubjectEntity subject);
}
