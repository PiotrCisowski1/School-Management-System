package com.cisowski.schoolmanagement.users.teacher.repository;

import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.common.repository.BaseUserRepository;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends BaseUserRepository<TeacherEntity, Integer> {
    boolean existsByTeachingSubjects(SubjectEntity subject);
    List<TeacherEntity> findByTeachingSubjects(SubjectEntity subject);

    @Query("SELECT s FROM SubjectEntity s JOIN s.teachers t WHERE t.id = :teacherId")
    List<SubjectEntity> findByTeacherId(@Param("teacherId") Integer teacherId);
}
