package com.cisowski.schoolmanagement.schedule.mapper;

import com.cisowski.schoolmanagement.classroom.mapper.ClassroomMapper;
import com.cisowski.schoolmanagement.common.mapper.DateMapper;
import com.cisowski.schoolmanagement.schedule.model.*;
import com.cisowski.schoolmanagement.subject.mapper.SubjectMapper;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherMapper;
import org.mapstruct.*;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {SubjectMapper.class, TeacherMapper.class, ClassroomMapper.class, ScheduleVersionMapper.class,
                DateMapper.class})
public interface ScheduleMapper {

    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "classroom", ignore = true)
    @Mapping(target = "scheduleVersion", ignore = true)
    @Mapping(target = "dayOfWeek",source = "dayOfWeek", qualifiedByName = "toDayOfWeek")
    ScheduleEntity toEntity(AddScheduleRequest request);

    @Mapping(target = "subject", source = "subject", qualifiedByName = "toSubjectSummaryResponse")
    @Mapping(target = "teacher", source = "teacher", qualifiedByName = "toTeacherSummaryResponse")
    @Mapping(target = "classroom", source = "classroom", qualifiedByName = "toClassroomSummaryResponse")
    @Mapping(target = "scheduleVersion", source = "scheduleVersion", qualifiedByName = "toScheduleVersionSummaryResponse")
    ScheduleDetailedResponse toDetailedResponse(ScheduleEntity saved);

    @Named("toScheduleDetailedResponseList")
    Collection<ScheduleDetailedResponse> toDetailedResponseList(Collection<ScheduleEntity> entities);

    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "classroom", ignore = true)
    @Mapping(target = "scheduleVersion", ignore = true)
    @Mapping(target = "dayOfWeek",source = "dayOfWeek", qualifiedByName = "toDayOfWeek")
    ScheduleEntity toEntity(PatchScheduleRequest request);

    List<ScheduleSummaryResponse> toSummaryResponseList(List<ScheduleEntity> entities);

    @Mapping(target = "classroomName", source = "entity.classroom.name")
    @Mapping(target = "subjectName", source = "entity.subject.name")
    @Mapping(target = "scheduleVersionId", source = "entity.scheduleVersion.id")
    @Mapping(target = "teacherName", expression = "java(entity.getTeacher().getFullName())")
    ScheduleSummaryResponse toSummaryResponse(ScheduleEntity entity);

    default void patchEntities(ScheduleEntity request, @MappingTarget ScheduleEntity existingEntity){
        if ( request == null || existingEntity == null) {
            return;
        }

        if (request.getSubject() != null) {
            existingEntity.setSubject(request.getSubject());
        }
        if (request.getTeacher() != null) {
            existingEntity.setTeacher(request.getTeacher());
        }
        if (request.getClassroom() != null) {
            existingEntity.setClassroom(request.getClassroom());
        }
        if (request.getScheduleVersion() != null) {
            existingEntity.setScheduleVersion(request.getScheduleVersion());
        }
        if (request.getDayOfWeek() != null) {
            existingEntity.setDayOfWeek(request.getDayOfWeek());
        }
        if (request.getStartTime() != null) {
            existingEntity.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            existingEntity.setEndTime(request.getEndTime());
        }
        if (request.getRecurrenceType() != null) {
            existingEntity.setRecurrenceType(request.getRecurrenceType());
        }
    }
}
