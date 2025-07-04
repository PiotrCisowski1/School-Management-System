package com.cisowski.schoolmanagement.grade.repository;

import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface GradeTypeRepository extends JpaRepository<GradeTypeEntity, BigInteger> {
    Optional<GradeTypeEntity> findByName(String name);
}
