package com.cisowski.schoolmanagement.timetable.attendance.mapper;

import com.cisowski.schoolmanagement.common.mapper.BaseMapperConfig;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceEntity;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceSummaryResponse;
import com.cisowski.schoolmanagement.users.student.mapper.StudentMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = BaseMapperConfig.class, uses = {StudentMapper.class})
public interface AttendanceMapper {

    List<AttendanceSummaryResponse> toSummaryResponseList(List<AttendanceEntity> entities);

    @Mapping(target = "student", source = "student", qualifiedByName = "toStudentSummaryResponse")
    @Mapping(target = "status", source = "attendanceStatus")
    AttendanceSummaryResponse toSummaryResponse(AttendanceEntity entity);
}
