package com.cisowski.schoolmanagement.users.teacher.service;

import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityRequest;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityResponse;

import java.util.Collection;


public interface TeacherAvailabilityService {
    TeacherAvailabilityResponse addTeacherAvailability(TeacherAvailabilityRequest request, Integer teacherId);
    void deleteTeacherAvailability(Integer teacherAvailabilityId);
    TeacherAvailabilityResponse getTeacherAvailabilityById(Integer teacherAvailabilityId);
    Collection<TeacherAvailabilityResponse> getTeacherAvailabilityByTeacherId(Integer teacherId);
    Collection<TeacherAvailabilityResponse> getTeacherAvailabilitiesByDayOfWeek(Integer dayOfWeek);

}
