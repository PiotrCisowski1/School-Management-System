package com.cisowski.schoolmanagement.yearbook.repository;

import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface YearbookRepository extends JpaRepository<YearbookEntity, Integer> {

    @Query("SELECT y FROM YearbookEntity y LEFT JOIN FETCH y.mainCourseSubjects WHERE y.symbol = :symbol OR y.headTeacher.id = :headTeacherId")
    Optional<YearbookEntity> findYearbookBySymbolOrHeadTeacher(@Param("symbol") String symbol, @Param("headTeacherId") Integer headTeacherId);
    boolean existsByHeadTeacher(TeacherEntity teacher);
    boolean existsByMainCourseSubjects(SubjectEntity subject);
}
