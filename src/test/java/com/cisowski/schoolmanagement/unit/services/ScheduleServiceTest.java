package com.cisowski.schoolmanagement.unit.services;

import com.cisowski.schoolmanagement.classroom.mapper.ClassroomMapper;
import com.cisowski.schoolmanagement.classroom.model.ClassroomEntity;
import com.cisowski.schoolmanagement.classroom.service.ClassroomService;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.mapper.DateMapper;
import com.cisowski.schoolmanagement.schedule.mapper.ScheduleMapper;
import com.cisowski.schoolmanagement.schedule.mapper.ScheduleVersionMapper;
import com.cisowski.schoolmanagement.schedule.model.AddScheduleRequest;
import com.cisowski.schoolmanagement.schedule.model.ScheduleDetailedResponse;
import com.cisowski.schoolmanagement.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.schedule.repository.ScheduleRepository;
import com.cisowski.schoolmanagement.schedule.service.ScheduleServiceImpl;
import com.cisowski.schoolmanagement.schedule.service.ScheduleVersionService;
import com.cisowski.schoolmanagement.subject.mapper.SubjectMapper;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.subject.service.SubjectService;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherMapper;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityEntity;
import com.cisowski.schoolmanagement.users.teacher.service.TeacherService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private SubjectService subjectService;
    @Mock
    private TeacherService teacherService;
    @Mock
    private ClassroomService classroomService;
    @Mock
    private ScheduleMapper scheduleMapperMocked;
    private ScheduleMapper scheduleMapper;
    @Mock
    private ScheduleVersionService scheduleVersionService;
    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    @BeforeEach
    public void setUp(){
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
    @DisplayName("add Schedule successful - should return ScheduleDetailedResponse")
    public void addSchedule_successful(){
        AddScheduleRequest request = Instancio.of(AddScheduleRequest.class)
                .generate(field("dayOfWeek"), gen -> gen.ints().range(1,3))
                .create();

        List<DayOfWeek> targetDays = List.of(DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        List<ScheduleEntity> existingSchedules = Instancio.ofList(ScheduleEntity.class)
                .generate(field(ScheduleEntity::getDayOfWeek), gen -> gen.oneOf(targetDays))
                .create();
        Integer scheduleVersionId = 123;
        ScheduleVersionEntity scheduleVersion = Instancio.of(ScheduleVersionEntity.class)
                .set(field(ScheduleVersionEntity::getId), scheduleVersionId)
                .set(field(ScheduleVersionEntity::getSchedules), existingSchedules)
                .create();
        ScheduleEntity schedule = scheduleMapper.toEntity(request);
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        TeacherAvailabilityEntity teacherAvailability = new TeacherAvailabilityEntity();
        teacherAvailability.setStartTime(schedule.getStartTime());
        teacherAvailability.setEndTime(schedule.getEndTime());
        teacherAvailability.setDayOfWeek(schedule.getDayOfWeek());
        teacher.getAvailability().add(teacherAvailability);
        ClassroomEntity classroom = Instancio.create(ClassroomEntity.class);
        schedule.setSubject(subject);
        schedule.setTeacher(teacher);
        schedule.setClassroom(classroom);
        schedule.setScheduleVersion(scheduleVersion);
        ScheduleDetailedResponse response = scheduleMapper.toDetailedResponse(schedule);

        when(scheduleVersionService.fetchScheduleVersion(scheduleVersionId)).thenReturn(scheduleVersion);
        when(scheduleMapperMocked.toEntity(request)).thenReturn(schedule);
        when(subjectService.fetchSubject(request.getSubjectId())).thenReturn(subject);
        when(teacherService.fetchTeacher(request.getTeacherId())).thenReturn(teacher);
        when(classroomService.fetchClassroom(request.getClassroomId())).thenReturn(classroom);
        when(scheduleRepository.findByClassroomAndTimeRange(
                classroom,
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime())).thenReturn(Optional.empty());
        when(scheduleRepository.save(schedule)).thenReturn(schedule);
        when(scheduleMapperMocked.toDetailedResponse(schedule)).thenReturn(response);

        ScheduleDetailedResponse result = scheduleService.addSchedule(request, scheduleVersionId);

        assertNotNull(result);
        assertEquals(response.getId(), result.getId());
        assertEquals(response.getSubject().getId(), result.getSubject().getId());
        assertEquals(response.getTeacher().getId(), result.getTeacher().getId());
        assertEquals(response.getClassroom().getId(), result.getClassroom().getId());
        assertEquals(response.getScheduleVersion().getId(), result.getScheduleVersion().getId());
        assertEquals(scheduleVersionId, result.getScheduleVersion().getId());
        assertEquals(response.getDayOfWeek(), result.getDayOfWeek());
        assertEquals(response.getStartTime(), result.getStartTime());
        assertEquals(response.getEndTime(), result.getEndTime());
        assertEquals(response.getRecurrenceType(), result.getRecurrenceType());
        verify(scheduleVersionService).fetchScheduleVersion(scheduleVersionId);
        verify(scheduleMapperMocked).toEntity(request);
        verify(subjectService).fetchSubject(request.getSubjectId());
        verify(teacherService).fetchTeacher(request.getTeacherId());
        verify(classroomService).fetchClassroom(request.getClassroomId());
        verify(scheduleRepository).findByClassroomAndTimeRange(
                classroom,
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime()
        );
        verify(scheduleRepository).save(schedule);
        verify(scheduleMapperMocked).toDetailedResponse(schedule);
    }

    @Test
    @DisplayName("addSchedule already appointed ")
    public void addSchedule_scheduleAlreadyAppointed(){
        AddScheduleRequest request = Instancio.of(AddScheduleRequest.class)
                .set(field(AddScheduleRequest::getDayOfWeek), 3)
                .set(field(AddScheduleRequest::getStartTime), LocalTime.of(10,0,0))
                .set(field(AddScheduleRequest::getEndTime), LocalTime.of(10,45,0))
                .create();

        Integer scheduleVersionId = 123;
        ScheduleVersionEntity scheduleVersion = new ScheduleVersionEntity();
        scheduleVersion.setId(scheduleVersionId);
        ScheduleEntity schedule = scheduleMapper.toEntity(request);
        ScheduleEntity appointedSchedule = new ScheduleEntity();
        appointedSchedule.setDayOfWeek(schedule.getDayOfWeek());
        appointedSchedule.setStartTime(schedule.getStartTime().minusHours(1));
        appointedSchedule.setEndTime(schedule.getEndTime().plusHours(1));
        scheduleVersion.setSchedules(Collections.singletonList(appointedSchedule));

        when(scheduleVersionService.fetchScheduleVersion(scheduleVersionId)).thenReturn(scheduleVersion);
        when(scheduleMapperMocked.toEntity(request)).thenReturn(schedule);

        SpecificationBrokenException result = assertThrows(
                SpecificationBrokenException.class,
                () -> scheduleService.addSchedule(request, scheduleVersionId));

        assertTrue(result.getMessage().contains("Schedule already appointed"));
        verify(scheduleVersionService).fetchScheduleVersion(scheduleVersionId);
        verify(scheduleMapperMocked).toEntity(request);
    }

    @Test
    @DisplayName("addSchedule Teacher not available")
    public void addSchedule_teacherNotAvailable(){
        AddScheduleRequest request = Instancio.of(AddScheduleRequest.class)
                .generate(field("dayOfWeek"), gen -> gen.ints().range(1,3))
                .create();
        Integer scheduleVersionId = 123;
        ScheduleEntity schedule = Instancio.create(ScheduleEntity.class);
        schedule.setDayOfWeek(DayOfWeek.FRIDAY);
        ScheduleVersionEntity scheduleVersion = Instancio.of(ScheduleVersionEntity.class)
                .set(field(ScheduleVersionEntity::getId), scheduleVersionId)
                .create();
        scheduleVersion.getSchedules().add(schedule);
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        ScheduleEntity mappedSchedule = scheduleMapper.toEntity(request);

        when(scheduleVersionService.fetchScheduleVersion(scheduleVersionId)).thenReturn(scheduleVersion);
        when(scheduleMapperMocked.toEntity(request)).thenReturn(mappedSchedule);
        when(subjectService.fetchSubject(request.getSubjectId())).thenReturn(subject);
        when(teacherService.fetchTeacher(request.getTeacherId())).thenReturn(teacher);

        assertThatThrownBy(() -> scheduleService.addSchedule(request, scheduleVersionId))
                .isInstanceOf(SpecificationBrokenException.class)
                .hasMessageContaining(
                        String.format(
                                "Teacher with ID: %s, is not available on %s at %s to %s",
                                teacher.getId(),
                                mappedSchedule.getDayOfWeek(),
                                mappedSchedule.getStartTime(),
                                mappedSchedule.getEndTime())
                );
    }

    @Test
    @DisplayName("addSchedule Classroom not available")
    public void addSchedule_classroomNotAvailable(){
        AddScheduleRequest request = Instancio.of(AddScheduleRequest.class)
                .generate(field("dayOfWeek"), gen -> gen.ints().range(1,7))
                .create();

        Integer scheduleVersionId = 123;
        ScheduleVersionEntity scheduleVersion = Instancio.create(ScheduleVersionEntity.class);
        scheduleVersion.setId(scheduleVersionId);
        ScheduleEntity schedule = scheduleMapper.toEntity(request);
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        TeacherAvailabilityEntity teacherAvailability = new TeacherAvailabilityEntity();
        teacherAvailability.setStartTime(schedule.getStartTime());
        teacherAvailability.setEndTime(schedule.getEndTime());
        teacherAvailability.setDayOfWeek(schedule.getDayOfWeek());
        teacher.getAvailability().add(teacherAvailability);
        ClassroomEntity classroom = Instancio.create(ClassroomEntity.class);

        when(scheduleVersionService.fetchScheduleVersion(scheduleVersionId)).thenReturn(scheduleVersion);
        when(scheduleMapperMocked.toEntity(request)).thenReturn(schedule);
        when(subjectService.fetchSubject(request.getSubjectId())).thenReturn(subject);
        when(teacherService.fetchTeacher(request.getTeacherId())).thenReturn(teacher);
        when(classroomService.fetchClassroom(request.getClassroomId())).thenReturn(classroom);
        when(scheduleRepository.findByClassroomAndTimeRange(any(),any(),any(),any())).thenReturn(Optional.of(schedule));

        SpecificationBrokenException result = assertThrows(
                SpecificationBrokenException.class,
                () -> scheduleService.addSchedule(request, scheduleVersionId)
        );
        assertTrue(result.getMessage().contains(
                String.format(
                        "Classroom with ID: %s is already booked on %s between %s and %s",
                        classroom.getId(),
                        schedule.getDayOfWeek(),
                        schedule.getStartTime(),
                        schedule.getEndTime())
        ));
    }

    @Test
    void deleteSchedule_successful(){
       Integer scheduleId = 1;
       ScheduleEntity schedule = new ScheduleEntity();

       when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

       scheduleService.deleteSchedule(scheduleId);

       verify(scheduleRepository).findById(scheduleId);
       verify(scheduleRepository).delete(schedule);
    }

    @Test
    @DisplayName("deleteSchedule with non existing object - should throw EntityNotFoundException")
    void deleteSchedule_scheduleNotfound(){
        Integer scheduleId = 1;

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->  scheduleService.deleteSchedule(scheduleId))
                .isInstanceOf(EntityNotFoundException.class);

        verify(scheduleRepository).findById(scheduleId);
    }
}
