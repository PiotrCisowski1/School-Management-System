package com.cisowski.schoolmanagement.subject.repository;

import com.cisowski.schoolmanagement.subject.model.SubjectTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectTypeRepository extends JpaRepository<SubjectTypeEntity, Integer> {
    Optional<SubjectTypeEntity> findByName(String name);
}
