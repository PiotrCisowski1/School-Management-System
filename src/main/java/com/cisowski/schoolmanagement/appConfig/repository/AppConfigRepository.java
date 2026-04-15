package com.cisowski.schoolmanagement.appConfig.repository;

import com.cisowski.schoolmanagement.appConfig.model.AppConfigEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppConfigRepository extends JpaRepository<AppConfigEntity, Long> {
    @EntityGraph(attributePaths = {"editableBy"})
    Optional<AppConfigEntity> findByKey(String key);
    Page<AppConfigEntity> findAllByIsEditable(boolean isEditable, Pageable pageable);
}
