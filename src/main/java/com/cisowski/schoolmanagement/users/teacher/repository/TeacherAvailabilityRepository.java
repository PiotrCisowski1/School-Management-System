package com.cisowski.schoolmanagement.users.teacher.repository;

import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherAvailabilityRepository extends JpaRepository<TeacherAvailabilityEntity, Integer> {
}
