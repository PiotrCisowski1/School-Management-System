package com.cisowski.schoolmanagement.subject.repository;

import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, Integer> {
    Optional<SubjectEntity> findSubjectByCode(String code);

    List<SubjectEntity> findSubjectsBySubjectType_Name(String subjectType);

    boolean existsBySubjectType(SubjectTypeEntity subjectType);
}
