package com.cisowski.schoolmanagement.users.teacher.service;

import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherAvailabilityMapper;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityEntity;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityRequest;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityResponse;
import com.cisowski.schoolmanagement.users.teacher.repository.TeacherAvailabilityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class TeacherAvailabilityServiceImpl implements TeacherAvailabilityService {
    private final TeacherAvailabilityRepository teacherAvailabilityRepository;
    private final TeacherService teacherService;
    private final TeacherAvailabilityMapper teacherAvailabilityMapper;
    @Override
    @Transactional
    public TeacherAvailabilityResponse addTeacherAvailability(TeacherAvailabilityRequest request, Integer teacherId) {
        DbLogger.info(String.format("Creating new TeacherAvailability for Teacher with ID %s, and request: %s", teacherId, request));
        TeacherEntity teacher = teacherService.fetchTeacher(teacherId);
        TeacherAvailabilityEntity requestEntity = teacherAvailabilityMapper.toEntity(request);
        requestEntity.setTeacher(teacher);
        checkHourOverlap(requestEntity);
        TeacherAvailabilityEntity saved = teacherAvailabilityRepository.save(requestEntity);
        DbLogger.info(String.format("TeacherAvailability for Teacher with ID: %s, was created successfully: %s", teacherId, saved.toString()));
        return teacherAvailabilityMapper.toResponse(saved);
    }

    private void checkHourOverlap(TeacherAvailabilityEntity entity){
        TeacherAvailabilityEntity existingAvailability = teacherAvailabilityRepository.findOverlappingAvailability(
                entity.getTeacher().getId(),
                entity.getDayOfWeek(),
                entity.getStartTime(),
                entity.getEndTime()
        );
        if(existingAvailability != null)
            throw new SpecificationBrokenException(String.format(
                    "There is already booked Availability on %s between %s and %s for Teacher with ID: %s",
                    existingAvailability.getDayOfWeek(),
                    existingAvailability.getStartTime(),
                    existingAvailability.getEndTime(),
                    existingAvailability.getTeacher().getId()));
    }

    @Override
    @Transactional
    public void deleteTeacherAvailability(Integer teacherAvailabilityId) {

    }

    @Override
    public TeacherAvailabilityResponse getTeacherAvailabilityById(Integer teacherAvailabilityId) {
        return null;
    }

    @Override
    public TeacherAvailabilityResponse getTeacherAvailabilityByTeacherId(Integer teacherId) {
        return null;
    }

    @Override
    public Collection<TeacherAvailabilityResponse> getTeacherAvailabilitiesByDayOfWeek(Integer dayOfWeek) {
        return null;
    }
}
