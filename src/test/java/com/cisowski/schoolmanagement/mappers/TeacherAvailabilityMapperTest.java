package com.cisowski.schoolmanagement.mappers;

import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherAvailabilityMapper;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherMapper;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityEntity;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityRequest;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityResponse;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;

public class TeacherAvailabilityMapperTest {

    private final TeacherAvailabilityMapper mapper = Mappers.getMapper(TeacherAvailabilityMapper.class);

    @BeforeEach
    public void setUp(){
        TeacherMapper teacherMapper = Mappers.getMapper(TeacherMapper.class);
        ReflectionTestUtils.setField(mapper, "teacherMapper", teacherMapper);
    }

    @Test
    public void toTeacherAvailabilityEntity(){
        TeacherAvailabilityRequest request = Instancio.create(TeacherAvailabilityRequest.class);
        request.setDayOfWeek(3);
        TeacherAvailabilityEntity result = mapper.toEntity(request);
        assertNotNull(result);
        assertEquals(request.getDayOfWeek(), request.getDayOfWeek().intValue());
        assertEquals(request.getStartTime(), result.getStartTime());
        assertEquals(request.getEndTime(), result.getEndTime());
        assertEquals(request.isAvailable(), result.isAvailable());
        assertEquals(request.getNotes(), result.getNotes());
    }

    @Test
    public void toTeacherAvailabilityResponse(){
        TeacherAvailabilityEntity entity = Instancio.create(TeacherAvailabilityEntity.class);
        entity.setDayOfWeek(DayOfWeek.FRIDAY);
        TeacherAvailabilityResponse result = mapper.toResponse(entity);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getTeacher().getId(), result.getTeacher().getId());
        assertEquals(entity.getDayOfWeek(), result.getDayOfWeek());
        assertEquals(entity.getStartTime(), result.getStartTime());
        assertEquals(entity.getEndTime(), result.getEndTime());
        assertEquals(entity.isAvailable(), result.isAvailable());
        assertEquals(entity.getNotes(), result.getNotes());
    }
}
