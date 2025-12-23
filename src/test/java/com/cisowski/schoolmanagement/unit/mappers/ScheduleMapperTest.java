package com.cisowski.schoolmanagement.unit.mappers;


import com.cisowski.schoolmanagement.classroom.mapper.ClassroomMapper;
import com.cisowski.schoolmanagement.common.mapper.DateMapper;
import com.cisowski.schoolmanagement.timetable.schedule.mapper.ScheduleMapper;
import com.cisowski.schoolmanagement.timetable.schedule.mapper.ScheduleVersionMapper;
import com.cisowski.schoolmanagement.timetable.schedule.model.AddScheduleRequest;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleDetailedResponse;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.subject.mapper.SubjectMapper;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ScheduleMapperTest {

    private ScheduleMapper scheduleMapper;

    @BeforeEach
    void setUp() {
        scheduleMapper = Mappers.getMapper(ScheduleMapper.class);

        DateMapper dateMapper = Mappers.getMapper(DateMapper.class);
        ReflectionTestUtils.setField(scheduleMapper, "dateMapper", dateMapper);
        SubjectMapper subjectMapper = Mappers.getMapper(SubjectMapper.class);
        ReflectionTestUtils.setField(scheduleMapper, "subjectMapper", subjectMapper);
        TeacherMapper teacherMapper = Mappers.getMapper(TeacherMapper.class);
        ReflectionTestUtils.setField(scheduleMapper, "teacherMapper", teacherMapper);
        ClassroomMapper classroomMapper = Mappers.getMapper(ClassroomMapper.class);
        ReflectionTestUtils.setField(scheduleMapper, "classroomMapper", classroomMapper);
        ScheduleVersionMapper scheduleVersionMapper = Mappers.getMapper(ScheduleVersionMapper.class);
        ReflectionTestUtils.setField(scheduleMapper, "scheduleVersionMapper", scheduleVersionMapper);
        ReflectionTestUtils.setField(subjectMapper, "teacherMapper", teacherMapper);
    }

    @Test
    @DisplayName("AddScheduleRequest to ScheduleEntity")
    void toEntity(){
        AddScheduleRequest request = Instancio.create(AddScheduleRequest.class);
        request.setDayOfWeek(4);

        ScheduleEntity result = scheduleMapper.toEntity(request);

        assertNotNull(result);
        assertEquals(request.getDayOfWeek(), result.getDayOfWeek().getValue());
        assertEquals(request.getStartTime(), result.getStartTime());
        assertEquals(request.getEndTime(), result.getEndTime());
        assertEquals(request.getRecurrenceType(), result.getRecurrenceType());
    }

    @Test
    @DisplayName("ScheduleEntity to ScheduleDetailedResponse")
    void toDetailedResponse(){
        ScheduleEntity entity = Instancio.create(ScheduleEntity.class);

        ScheduleDetailedResponse result = scheduleMapper.toDetailedResponse(entity);

        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getSubject().getId(), result.getSubject().getId());
        assertEquals(entity.getTeacher().getId(), result.getTeacher().getId());
        assertEquals(entity.getClassroom().getId(), result.getClassroom().getId());
        assertEquals(entity.getScheduleVersion().getId(), result.getScheduleVersion().getId());
        assertEquals(entity.getDayOfWeek(), result.getDayOfWeek());
        assertEquals(entity.getStartTime(), result.getStartTime());
        assertEquals(entity.getEndTime(), result.getEndTime());
        assertEquals(entity.getRecurrenceType(), result.getRecurrenceType());

    }
}
