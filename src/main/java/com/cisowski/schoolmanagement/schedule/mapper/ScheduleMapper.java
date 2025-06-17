package com.cisowski.schoolmanagement.schedule.mapper;

import com.cisowski.schoolmanagement.classroom.mapper.ClassroomMapper;
import com.cisowski.schoolmanagement.classroom.model.ClassroomEntity;
import com.cisowski.schoolmanagement.common.mapper.DateMapper;
import com.cisowski.schoolmanagement.schedule.model.AddScheduleRequest;
import com.cisowski.schoolmanagement.schedule.model.PatchScheduleRequest;
import com.cisowski.schoolmanagement.schedule.model.ScheduleDetailedResponse;
import com.cisowski.schoolmanagement.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.subject.mapper.SubjectMapper;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherMapper;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

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

    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "classroom", ignore = true)
    @Mapping(target = "scheduleVersion", ignore = true)
    @Mapping(target = "dayOfWeek",source = "dayOfWeek", qualifiedByName = "toDayOfWeek")
    ScheduleEntity toEntity(PatchScheduleRequest request);


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
