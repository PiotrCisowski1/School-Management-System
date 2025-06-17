package com.cisowski.schoolmanagement.schedule.service;

import com.cisowski.schoolmanagement.classroom.model.ClassroomEntity;
import com.cisowski.schoolmanagement.classroom.service.ClassroomService;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.schedule.mapper.ScheduleMapper;
import com.cisowski.schoolmanagement.schedule.model.*;
import com.cisowski.schoolmanagement.schedule.repository.ScheduleRepository;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.subject.service.SubjectService;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityEntity;
import com.cisowski.schoolmanagement.users.teacher.service.TeacherService;
import com.cisowski.schoolmanagement.users.teacher.utils.TeacherAvailabilityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final SubjectService subjectService;
    private final TeacherService teacherService;
    private final ClassroomService classroomService;
    private final ScheduleMapper scheduleMapper;
    private final ScheduleVersionService scheduleVersionService;

    @Override
    @Transactional
    public ScheduleDetailedResponse addSchedule(AddScheduleRequest request, Integer scheduleVersionId) {
        DbLogger.info(String.format("Adding Schedule in ScheduleVersion with ID %s, with given request: %s", scheduleVersionId, request));

        ScheduleVersionEntity scheduleVersion = scheduleVersionService.fetchScheduleVersion(scheduleVersionId);
        ScheduleEntity schedule = scheduleMapper.toEntity(request);

        checkIfScheduleAlreadyAppointed(scheduleVersion, schedule);

        SubjectEntity subject = subjectService.fetchSubject(request.getSubjectId());
        TeacherEntity teacher = teacherService.fetchTeacher(request.getTeacherId());
        checkTeacherAvailability(teacher, schedule.getDayOfWeek(), schedule.getStartTime(), schedule.getEndTime());
        ClassroomEntity classroom = classroomService.fetchClassroom(request.getClassroomId());
        checkClassroomAvailability(classroom, schedule.getDayOfWeek(), schedule.getStartTime(), schedule.getEndTime());
        schedule.setSubject(subject);
        schedule.setTeacher(teacher);
        schedule.setClassroom(classroom);
        schedule.setScheduleVersion(scheduleVersion);

        ScheduleEntity saved = scheduleRepository.save(schedule);
        DbLogger.info(String.format("Schedule was saved successfully in ScheduleVersion with ID %s: %s", scheduleVersionId, saved.toString()));

        return scheduleMapper.toDetailedResponse(saved);
    }

    private void checkIfScheduleAlreadyAppointed(ScheduleVersionEntity scheduleVersion, ScheduleEntity schedule){
        if(scheduleVersion == null || schedule == null)
            throw new SpecificationBrokenException("Given ScheduleVersion or Schedule is not a valid object");
        if(scheduleVersion.getSchedules() != null){
            Optional<ScheduleEntity> existingSchedule = scheduleVersion.getSchedules().stream()
                    .filter(Objects::nonNull)
                    .filter(scheduleEntity -> scheduleEntity.getDayOfWeek().equals(schedule.getDayOfWeek()))
                    .filter(scheduleEntity -> scheduleEntity.getStartTime().isBefore(schedule.getEndTime()))
                    .filter(scheduleEntity -> scheduleEntity.getEndTime().isAfter(schedule.getStartTime()))
                    .findFirst();
            if(existingSchedule.isPresent())
                throw new SpecificationBrokenException(String.format(
                        "Schedule already appointed between %s and %s on %s",
                        existingSchedule.get().getStartTime().toString(),
                        existingSchedule.get().getEndTime().toString(),
                        existingSchedule.get().getDayOfWeek().toString()));
        }
    }

    private void checkTeacherAvailability(TeacherEntity teacher, DayOfWeek day, LocalTime startTime, LocalTime endTime){
        if(!CollectionUtils.isEmpty(teacher.getAvailability())){
            Collection<TeacherAvailabilityEntity> availabilities = teacher.getAvailability().stream()
                    .filter(Objects::nonNull)
                    .filter(availability -> TeacherAvailabilityUtils.isAvailable(availability, day, startTime, endTime))
                    .toList();
            if(availabilities.isEmpty())
                throw new SpecificationBrokenException(String.format(
                        "Teacher with ID: %s, is not available on %s at %s to %s",
                        teacher.getId(),
                        day,
                        startTime,
                        endTime));
        }

    }

    private void checkClassroomAvailability(ClassroomEntity classroom, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        Optional<ScheduleEntity> existingEntity = scheduleRepository.findByClassroomAndTimeRange(
                classroom,
                dayOfWeek,
                startTime,
                endTime
        );
        if(existingEntity.isPresent())
            throw new SpecificationBrokenException(String.format(
                    "Classroom with ID: %s is already booked on %s between %s and %s",
                    classroom.getId(),
                    existingEntity.get().getDayOfWeek(),
                    existingEntity.get().getStartTime(),
                    existingEntity.get().getEndTime()
                    ));
    }

    @Override
    public void deleteSchedule(Integer scheduleId) {
        DbLogger.info(String.format("Deleting Schedule with ID %s", scheduleId));

        Optional<ScheduleEntity> schedule = scheduleRepository.findById(scheduleId);
        if(schedule.isEmpty())
            throw new EntityNotFoundException(ScheduleEntity.class, "ID", scheduleId.toString());

        scheduleRepository.delete(schedule.get());
        DbLogger.info(String.format("Schedule with ID %s was deleted successfully", scheduleId));
    }

    @Override
    public ScheduleDetailedResponse patchSchedule(Integer scheduleId, PatchScheduleRequest request) {
        DbLogger.info(String.format("Updating Schedule with ID %s, with given request: %s", scheduleId, request));

        Optional<ScheduleEntity> schedule = scheduleRepository.findById(scheduleId);
        if(schedule.isEmpty())
            throw new EntityNotFoundException(ScheduleEntity.class, "ID", scheduleId.toString());

        ScheduleEntity requestSchedule = scheduleMapper.toEntity(request);
        SubjectEntity subject = fetchSubject(request.getSubjectId());
        requestSchedule.setSubject(subject);
        TeacherEntity teacher = fetchTeacher(request.getTeacherId());
        if(teacher != null)
            checkTeacherAvailability(teacher, requestSchedule.getDayOfWeek(), requestSchedule.getStartTime(), requestSchedule.getEndTime());
        requestSchedule.setTeacher(teacher);
        ClassroomEntity classroom = fetchClassroom(request.getClassroomId());
        if(classroom != null)
            checkClassroomAvailability(classroom, requestSchedule.getDayOfWeek(), requestSchedule.getStartTime(), requestSchedule.getEndTime());
        requestSchedule.setClassroom(classroom);

        ScheduleEntity existingSchedule = schedule.get();
        scheduleMapper.patchEntities(requestSchedule, existingSchedule);

        ScheduleEntity savedSchedule = scheduleRepository.save(existingSchedule);
        DbLogger.info(String.format("Schedule with ID %s was updated successfully: %s", scheduleId, savedSchedule.toString()));

        return scheduleMapper.toDetailedResponse(savedSchedule);
    }

    private SubjectEntity fetchSubject(Integer subjectId){
        if(subjectId == null)
            return null;
        return subjectService.fetchSubject(subjectId);
    }

    private TeacherEntity fetchTeacher(Integer teacherId){
        if(teacherId == null)
            return null;
        return teacherService.fetchTeacher(teacherId);
    }

    private ClassroomEntity fetchClassroom(Integer classroomId){
        if(classroomId == null)
            return null;
        return classroomService.fetchClassroom(classroomId);
    }
}
