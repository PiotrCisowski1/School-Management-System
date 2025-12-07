package com.cisowski.schoolmanagement.appConfig.repository;

import com.cisowski.schoolmanagement.appConfig.model.AppConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppConfigRepository extends JpaRepository<AppConfigEntity, Long> {
    Optional<AppConfigEntity> findByKey(String key);
    List<AppConfigEntity> findAllByIsEditable(boolean isEditable);
}
