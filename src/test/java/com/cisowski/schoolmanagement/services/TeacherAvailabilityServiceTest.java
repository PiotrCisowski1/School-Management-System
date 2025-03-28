package com.cisowski.schoolmanagement.services;

import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherAvailabilityMapper;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityEntity;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityRequest;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityResponse;
import com.cisowski.schoolmanagement.users.teacher.repository.TeacherAvailabilityRepository;
import com.cisowski.schoolmanagement.users.teacher.service.TeacherAvailabilityServiceImpl;
import com.cisowski.schoolmanagement.users.teacher.service.TeacherService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherAvailabilityServiceTest {
    @Mock
    private TeacherAvailabilityRepository teacherAvailabilityRepository;
    @Mock
    private TeacherService teacherService;
    @Mock
    private TeacherAvailabilityMapper teacherAvailabilityMapper;
    @InjectMocks
    private TeacherAvailabilityServiceImpl teacherAvailabilityService;

    private TeacherAvailabilityRequest request;
    private TeacherAvailabilityEntity requestEntity;
    private TeacherEntity teacher;
    private TeacherAvailabilityResponse response;

    @BeforeEach
    public void setUp(){
        request = new TeacherAvailabilityRequest(
             4,
                Time.valueOf("08:30:00"),
                Time.valueOf("10:00:00"),
                true,
                "The notes"
        );
        teacher = Instancio.create(TeacherEntity.class);

        requestEntity = new TeacherAvailabilityEntity();
        requestEntity.setId(1);
        requestEntity.setTeacher(teacher);
        requestEntity.setDayOfWeek(DayOfWeek.of(request.getDayOfWeek()));
        requestEntity.setStartTime(request.getStartTime());
        requestEntity.setEndTime(request.getEndTime());
        requestEntity.setAvailable(request.isAvailable());
        requestEntity.setNotes(request.getNotes());

        TeacherSummaryResponse teacherSummaryResponse = new TeacherSummaryResponse();
        teacherSummaryResponse.setId(teacher.getId());

        response = new TeacherAvailabilityResponse();
        response.setId(requestEntity.getId());
        response.setTeacher(teacherSummaryResponse);
        response.setDayOfWeek(requestEntity.getDayOfWeek());
        response.setStartTime(requestEntity.getStartTime());
        response.setEndTime(requestEntity.getEndTime());
        response.setAvailable(requestEntity.isAvailable());
        response.setNotes(requestEntity.getNotes());
    }


    @Test
    @DisplayName("addTeacherAvailability successful - should return TeacherAvailabilityResponse")
    public void addTeacherAvailability_successful(){
        when(teacherService.fetchTeacher(teacher.getId())).thenReturn(teacher);
        when(teacherAvailabilityMapper.toEntity(request)).thenReturn(requestEntity);
        when(teacherAvailabilityRepository.findOverlappingAvailability(
                requestEntity.getTeacher().getId(),
                requestEntity.getDayOfWeek(),
                requestEntity.getStartTime(),
                requestEntity.getEndTime()))
                .thenReturn(null);
        when(teacherAvailabilityRepository.save(requestEntity)).thenReturn(requestEntity);
        when(teacherAvailabilityMapper.toResponse(requestEntity)).thenReturn(response);

        TeacherAvailabilityResponse result = teacherAvailabilityService.addTeacherAvailability(request, teacher.getId());

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(request.getDayOfWeek(), result.getDayOfWeek().getValue());
        assertEquals(request.getStartTime(), result.getStartTime());
        assertEquals(request.getEndTime(), result.getEndTime());
        assertEquals(request.isAvailable(), result.isAvailable());
        assertEquals(request.getNotes(), result.getNotes());
        assertEquals(teacher.getId(), result.getTeacher().getId());
        verify(teacherService).fetchTeacher(teacher.getId());
        verify(teacherAvailabilityMapper).toEntity(request);
        verify(teacherAvailabilityRepository).findOverlappingAvailability(
                requestEntity.getTeacher().getId(),
                requestEntity.getDayOfWeek(),
                requestEntity.getStartTime(),
                requestEntity.getEndTime());
        verify(teacherAvailabilityRepository).save(requestEntity);
        verify(teacherAvailabilityMapper).toResponse(requestEntity);
    }

    @Test
    @DisplayName("addTeacherAvailability hours overlap - should throw SpecificationBrokenException")
    public void addTeacherAvailability_hoursOverlap(){
        when(teacherService.fetchTeacher(teacher.getId())).thenReturn(teacher);
        when(teacherAvailabilityMapper.toEntity(request)).thenReturn(requestEntity);
        when(teacherAvailabilityRepository.findOverlappingAvailability(
                requestEntity.getTeacher().getId(),
                requestEntity.getDayOfWeek(),
                requestEntity.getStartTime(),
                requestEntity.getEndTime()))
                .thenReturn(requestEntity);

        SpecificationBrokenException result = assertThrows(
                SpecificationBrokenException.class,
                () -> teacherAvailabilityService.addTeacherAvailability(request, teacher.getId()));
        assertNotNull(result);
        assertTrue(result.getMessage().contains("already booked Availability"));
        assertTrue(result.getMessage().contains(requestEntity.getDayOfWeek().toString()));
        assertTrue(result.getMessage().contains(requestEntity.getStartTime().toString()));
        assertTrue(result.getMessage().contains(requestEntity.getEndTime().toString()));
        assertTrue(result.getMessage().contains(requestEntity.getTeacher().getId().toString()));
    }

    @Test
    public void deleteTeacherAvailability_successful(){
        when(teacherAvailabilityRepository.findById(1)).thenReturn(Optional.of(requestEntity));
        doNothing().when(teacherAvailabilityRepository).delete(requestEntity);

        teacherAvailabilityService.deleteTeacherAvailability(1);

        verify(teacherAvailabilityRepository).findById(1);
        verify(teacherAvailabilityRepository).delete(requestEntity);
    }

    @Test
    @DisplayName("deleteTeacherAvailability no availability found - should throw EntityNotFoundException")
    public void deleteTeacherAvailability_noEntity(){
        when(teacherAvailabilityRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException result = assertThrows(
                EntityNotFoundException.class,
                () -> teacherAvailabilityService.deleteTeacherAvailability(1));

        verify(teacherAvailabilityRepository).findById(1);
        assertTrue(result.getMessage().contains("ID"));
        assertTrue(result.getMessage().contains("1"));
    }
}
