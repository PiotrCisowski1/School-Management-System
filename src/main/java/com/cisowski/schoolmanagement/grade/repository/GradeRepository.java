package com.cisowski.schoolmanagement.grade.repository;

import com.cisowski.schoolmanagement.grade.model.grade.GradeEntity;
import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeValueEntity;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<GradeEntity, BigInteger> {
    boolean existsByGradeType(GradeTypeEntity type);
    boolean existsByGradeValue(GradeValueEntity gradeValue);
    boolean existsByGradeValueIn(List<GradeValueEntity> gradeValues);
}
