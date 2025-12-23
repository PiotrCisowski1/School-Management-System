package com.cisowski.schoolmanagement.grade.repository;

import com.cisowski.schoolmanagement.grade.model.grade.GradeEntity;
import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeValueEntity;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<GradeEntity, Long> {
    boolean existsByGradeType(GradeTypeEntity type);
    boolean existsByGradeValue(GradeValueEntity gradeValue);
    boolean existsByGradeValueIn(List<GradeValueEntity> gradeValues);
    List<GradeEntity> findByStudent(StudentEntity student);
    List<GradeEntity> findByStudentAndSubject(StudentEntity student, SubjectEntity subject);
    List<GradeEntity> findBySubject(SubjectEntity subject);
    List<GradeEntity> findByGradeType(GradeTypeEntity gradeType);

    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM GradeEntity g WHERE g.id = :gradeId AND g.teacher.id = :teacherId")
    boolean existsByIdAndTeacherId(@Param("gradeId") Long id, @Param("teacherId") Integer teacherId);
}
