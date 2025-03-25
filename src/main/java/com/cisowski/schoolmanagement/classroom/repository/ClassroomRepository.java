package com.cisowski.schoolmanagement.classroom.repository;

import com.cisowski.schoolmanagement.classroom.model.ClassroomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassroomRepository extends JpaRepository<ClassroomEntity, Integer> {
}
