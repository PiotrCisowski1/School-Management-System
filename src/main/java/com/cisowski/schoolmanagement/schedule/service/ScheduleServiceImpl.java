package com.cisowski.schoolmanagement.schedule.service;

import com.cisowski.schoolmanagement.classroom.model.ClassroomEntity;
import com.cisowski.schoolmanagement.classroom.service.ClassroomService;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.schedule.mapper.ScheduleMapper;
import com.cisowski.schoolmanagement.schedule.model.*;
import com.cisowski.schoolmanagement.schedule.model.scheduleChangelog.ScheduleChangeType;
import com.cisowski.schoolmanagement.schedule.model.scheduleChangelog.ScheduleChangelogDto;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.schedule.repository.ScheduleRepository;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.subject.service.SubjectService;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityEntity;
import com.cisowski.schoolmanagement.users.teacher.service.TeacherService;
import com.cisowski.schoolmanagement.users.teacher.utils.TeacherAvailabilityUtils;
import io.jsonwebtoken.lang.Collections;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final SubjectService subjectService;
    private final TeacherService teacherService;
    private final ClassroomService classroomService;
    private final ScheduleMapper scheduleMapper;
    private final ScheduleVersionService scheduleVersionService;
    private final ScheduleChangelogService scheduleChangelogService;
    private final ScheduleStatusService scheduleStatusService;

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

        List<UserEntity> usersAffectedBySchedule = scheduleStatusService.createListWithUsersAffectedByChange(saved);
        ScheduleChangelogDto changelogDto = new ScheduleChangelogDto(
                schedule,
                ScheduleChangeType.CREATED,
                "entity",
                null,
                "entity",
                "New Schedule created",
                true,
                usersAffectedBySchedule);
        scheduleChangelogService.logChange(changelogDto);

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
        if(!CollectionUtils.isEmpty(teacher.getAvailability())) {
            Collection<TeacherAvailabilityEntity> availabilities = teacher.getAvailability().stream()
                    .filter(Objects::nonNull)
                    .filter(availability -> TeacherAvailabilityUtils.isAvailable(availability, day, startTime, endTime))
                    .toList();
            if (!availabilities.isEmpty())
                return;
        }

        throw new SpecificationBrokenException(String.format(
                "Teacher with ID: %s, is not available on %s at %s to %s",
                teacher.getId(),
                day,
                startTime,
                endTime));
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
    @Transactional
    public void deleteSchedule(Integer scheduleId) {
        DbLogger.info(String.format("Marking Schedule with ID %s as deleted", scheduleId));

        Optional<ScheduleEntity> existingSchedule = scheduleRepository.findById(scheduleId);
        if(existingSchedule.isEmpty())
            throw new EntityNotFoundException(ScheduleEntity.class, "ID", scheduleId.toString());

        ScheduleEntity schedule = existingSchedule.get();
        if(scheduleStatusService.isAlreadyDeleted(schedule))
            throw new SpecificationBrokenException(String.format("Schedule with ID %s is marked as deleted", schedule.getId()));

        scheduleStatusService.changeStatusToDeleted(schedule);
        scheduleRepository.save(schedule);
        DbLogger.info(String.format("Schedule with ID %s was marked as deleted successfully", scheduleId));
    }

    @Override
    @Transactional
    public ScheduleDetailedResponse patchSchedule(Integer scheduleId, PatchScheduleRequest request) {
        DbLogger.info(String.format("Updating Schedule with ID %s, with given request: %s", scheduleId, request));

        Optional<ScheduleEntity> schedule = scheduleRepository.findById(scheduleId);
        if(schedule.isEmpty())
            throw new EntityNotFoundException(ScheduleEntity.class, "ID", scheduleId.toString());
        if(scheduleStatusService.isAlreadyDeleted(schedule.get()))
            throw new SpecificationBrokenException(String.format("Schedule with ID %s is marked as deleted", schedule.get().getId()));

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
        logPatchChanges(requestSchedule, existingSchedule, request.getUpdateReason());
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

    @Override
    public ScheduleDetailedResponse getSchedule(Integer scheduleId) {
        DbLogger.info(String.format("Searching for Schedule with ID: %s", scheduleId));

        Optional<ScheduleEntity> schedule = scheduleRepository.findById(scheduleId);
        if(schedule.isEmpty())
            throw new EntityNotFoundException(ScheduleEntity.class, "ID", scheduleId.toString());
        if(scheduleStatusService.isAlreadyDeleted(schedule.get()))
            throw new SpecificationBrokenException(String.format("Schedule with ID %s is marked as deleted", schedule.get().getId()));

        DbLogger.info(String.format("Found Schedule with ID: %s", scheduleId));
        return scheduleMapper.toDetailedResponse(schedule.get());
    }

    @Override
    public List<ScheduleSummaryResponse> getScheduleByDayOfWeek(Integer scheduleVersionId, Integer dayOfWeek, boolean needsFiltering, Integer userId) {
        DbLogger.info(String.format("Searching for Schedules for DayOfWeek: %s in ScheduleVersion with ID: %s", dayOfWeek, scheduleVersionId));
        DayOfWeek day = DayOfWeek.of(dayOfWeek);

        List<ScheduleEntity> schedules = scheduleRepository.findByScheduleVersionIdAndDayOfWeek(scheduleVersionId, day);
        List<ScheduleEntity> notDeletedSchedules = scheduleStatusService.filterDeletedSchedules(schedules);
        List<ScheduleEntity> filteredSchedules = notDeletedSchedules;
        if(needsFiltering)
            filteredSchedules = filterTeacherSchedules(notDeletedSchedules, userId);

        DbLogger.info(String.format("Found %s Schedules for DayOfWeek: %s in ScheduleVersion with ID: %s", filteredSchedules.size(), dayOfWeek, scheduleVersionId));
        return scheduleMapper.toSummaryResponseList(filteredSchedules);
    }

    @Override
    public List<ScheduleEntity> fetchSchedulesByClassroomId(Integer classroomId) {
        DbLogger.info("Searching for Schedules for Classroom with ID: " + classroomId);
        List<ScheduleEntity> schedules = scheduleRepository.findByClassroomId(classroomId);
        DbLogger.info(String.format("Found %s Schedules for Classroom with ID: %s", schedules.size(), classroomId));
        return scheduleStatusService.filterDeletedSchedules(schedules);
    }

    private List<ScheduleEntity> filterTeacherSchedules(List<ScheduleEntity> schedules, Integer authenticatedTeacherId) {
        if (!Collections.isEmpty(schedules)){
            DbLogger.info("Restricting list of Schedules for authenticated Teacher");
            return schedules.stream()
                    .filter(Objects::nonNull)
                    .filter(schedule -> schedule.getTeacher().getId().equals(authenticatedTeacherId))
                    .collect(Collectors.toList());
        }

        return schedules;
    }

    private void logPatchChanges(ScheduleEntity requestSchedule, ScheduleEntity existingSchedule, String changeReason) {
        if(requestSchedule == null || existingSchedule == null)
            return;
        List<UserEntity> affectedUsers = scheduleStatusService.createListWithUsersAffectedByChange(existingSchedule);
        boolean anyChanges = false;
        if(requestSchedule.getSubject() != null && !requestSchedule.getSubject().getId().equals(existingSchedule.getSubject().getId())) {
            ScheduleChangelogDto changelogDto = new ScheduleChangelogDto(
                    existingSchedule,
                    ScheduleChangeType.SUBJECT_CHANGED,
                    "subject.id",
                    existingSchedule.getSubject().getId().toString(),
                    requestSchedule.getSubject().getId().toString(),
                    changeReason,
                    false,
                    affectedUsers);
            scheduleChangelogService.logChange(changelogDto);
            anyChanges = true;
        }
        if(requestSchedule.getTeacher() != null && !requestSchedule.getTeacher().getId().equals(existingSchedule.getTeacher().getId())) {
            ScheduleChangelogDto changelogDto = new ScheduleChangelogDto(
                    existingSchedule,
                    ScheduleChangeType.TEACHER_CHANGED,
                    "teacher.id",
                    existingSchedule.getTeacher().getId().toString(),
                    requestSchedule.getTeacher().getId().toString(),
                    changeReason,
                    false,
                    affectedUsers);
            scheduleChangelogService.logChange(changelogDto);
            anyChanges = true;
        }
        if(requestSchedule.getClassroom() != null && !requestSchedule.getClassroom().getId().equals(existingSchedule.getClassroom().getId())) {
            ScheduleChangelogDto changelogDto = new ScheduleChangelogDto(
                    existingSchedule,
                    ScheduleChangeType.CLASSROOM_CHANGED,
                    "classroom.id",
                    existingSchedule.getClassroom().getId().toString(),
                    requestSchedule.getClassroom().getId().toString(),
                    changeReason,
                    false,
                    affectedUsers);
            scheduleChangelogService.logChange(changelogDto);
            anyChanges = true;
        }
        if(requestSchedule.getDayOfWeek() != null && !requestSchedule.getDayOfWeek().equals(existingSchedule.getDayOfWeek())) {
            ScheduleChangelogDto changelogDto = new ScheduleChangelogDto(
                    existingSchedule,
                    ScheduleChangeType.RESCHEDULED,
                    "dayOfWeek",
                    existingSchedule.getDayOfWeek().toString(),
                    requestSchedule.getDayOfWeek().toString(),
                    changeReason,
                    false,
                    affectedUsers);
            scheduleChangelogService.logChange(changelogDto);
            anyChanges = true;
        }
        if(requestSchedule.getStartTime() != null && !requestSchedule.getStartTime().equals(existingSchedule.getStartTime())) {
            ScheduleChangelogDto changelogDto = new ScheduleChangelogDto(
                    existingSchedule,
                    ScheduleChangeType.RESCHEDULED,
                    "startTime",
                    existingSchedule.getStartTime().toString(),
                    requestSchedule.getStartTime().toString(),
                    changeReason,
                    false,
                    affectedUsers);
            scheduleChangelogService.logChange(changelogDto);
            anyChanges = true;
        }
        if(requestSchedule.getEndTime() != null && !requestSchedule.getEndTime().equals(existingSchedule.getEndTime())) {
            ScheduleChangelogDto changelogDto = new ScheduleChangelogDto(
                    existingSchedule,
                    ScheduleChangeType.RESCHEDULED,
                    "endTime",
                    existingSchedule.getEndTime().toString(),
                    requestSchedule.getEndTime().toString(),
                    changeReason,
                    false,
                    affectedUsers);
            scheduleChangelogService.logChange(changelogDto);
            anyChanges = true;
        }
        if(requestSchedule.getRecurrenceType() != null && !requestSchedule.getRecurrenceType().equals(existingSchedule.getRecurrenceType())) {
            ScheduleChangelogDto changelogDto = new ScheduleChangelogDto(
                    existingSchedule,
                    ScheduleChangeType.RESCHEDULED,
                    "recurrence",
                    existingSchedule.getRecurrenceType().toString(),
                    requestSchedule.getRecurrenceType().toString(),
                    changeReason,
                    false,
                    affectedUsers);
            scheduleChangelogService.logChange(changelogDto);
            anyChanges = true;
        }
        if(anyChanges)
            existingSchedule.setStatus(ScheduleStatus.UPDATED);
    }
}
