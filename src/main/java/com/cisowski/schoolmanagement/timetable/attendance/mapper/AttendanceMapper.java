package com.cisowski.schoolmanagement.timetable.attendance.mapper;

import com.cisowski.schoolmanagement.common.mapper.BaseMapperConfig;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceDetailedResponse;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceEntity;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceSummaryResponse;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.mapper.ScheduleOccurrenceMapper;
import com.cisowski.schoolmanagement.users.common.mapper.BaseUserMapper;
import com.cisowski.schoolmanagement.users.student.mapper.StudentMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = BaseMapperConfig.class, uses = {StudentMapper.class, ScheduleOccurrenceMapper.class, BaseUserMapper.class})
public interface AttendanceMapper {

    List<AttendanceSummaryResponse> toSummaryResponseList(List<AttendanceEntity> entities);

    @Mapping(target = "student", source = "student", qualifiedByName = "toStudentSummaryResponse")
    @Mapping(target = "scheduleOccurrence", source = "occurrence", qualifiedByName = "toScheduleOccurrenceSummaryResponse")
    @Mapping(target = "status", source = "attendanceStatus")
    AttendanceSummaryResponse toSummaryResponse(AttendanceEntity entity);

    @Mapping(target = "student", source = "student", qualifiedByName = "toStudentSummaryResponse")
    @Mapping(target = "occurrence", source = "occurrence", qualifiedByName = "toScheduleOccurrenceSummaryResponse")
    @Mapping(target = "status", source = "attendanceStatus")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy", qualifiedByName = "toUserSummaryResponse")
    AttendanceDetailedResponse toDetailedResponse(AttendanceEntity attendanceEntity);

}
