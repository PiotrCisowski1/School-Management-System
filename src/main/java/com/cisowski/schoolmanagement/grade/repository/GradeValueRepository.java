package com.cisowski.schoolmanagement.grade.repository;

import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeScaleEntity;
import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeValueRepository extends JpaRepository<GradeValueEntity, Long> {

    Optional<GradeValueEntity> findByGradeScaleAndId(GradeScaleEntity gradeScale, Long gradeValueId);
}
