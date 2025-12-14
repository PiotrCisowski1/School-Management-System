package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceEntity;
import com.cisowski.schoolmanagement.timetable.attendance.service.AttendanceService;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.service.ScheduleOccurrenceService;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherAttendancePermissionHandler extends BaseResourcePermissionHandler<AttendanceEntity> {

    private final ScheduleOccurrenceService occurrenceService;
    private final AttendanceService attendanceService;

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(String.format("Checking if Teacher can access %s for action %s", context.getResourceType(), context.getAction()));
        return switch (accessContext.getActionType()) {
            case CREATE, READ -> checkTeacherReadPermission(context, accessContext);
            default -> false;
        };
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.TEACHER;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.ATTENDANCE;
    }

    private boolean checkTeacherReadPermission(PermissionContext context, ResourceAccessContext accessContext) {
        TeacherEntity teacher = context.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY);
        if(teacher == null)
            return false;
        ScheduleOccurrenceEntity occurrence = null;
        Long scheduleOccurrenceId = accessContext.getAccessedMethodParameter("scheduleOccurrenceId");
        if(scheduleOccurrenceId != null)
             occurrence = occurrenceService.fetchScheduleOccurrence(scheduleOccurrenceId);
        Long attendanceId = accessContext.getAccessedMethodParameter("attendanceId");
        if(attendanceId != null) {
            AttendanceEntity attendance = attendanceService.fetchAttendance(attendanceId);
            occurrence = attendance.getOccurrence();
        }
        if(occurrence != null)
            return checkTeacherOwnsOccurrence(occurrence, teacher);

        return false;
    }

    private boolean checkTeacherOwnsOccurrence(ScheduleOccurrenceEntity occurrence, TeacherEntity teacher) {
        return occurrence.getSchedule().getTeacher().getId().equals(teacher.getId());
    }
}
