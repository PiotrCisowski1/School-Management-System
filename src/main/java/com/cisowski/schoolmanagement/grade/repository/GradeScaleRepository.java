package com.cisowski.schoolmanagement.grade.repository;

import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeScaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeScaleRepository extends JpaRepository<GradeScaleEntity, Long> {

    Optional<GradeScaleEntity> findByIsActive(boolean isActive);
    Optional<GradeScaleEntity> findFirstByIdNotOrderByCreatedAtDesc(Long id);
}
