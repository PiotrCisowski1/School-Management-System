package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.service.ScheduleOccurrenceService;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherAttendancePermissionHandler extends BaseResourcePermissionHandler<AttendanceEntity> {

    private final ScheduleOccurrenceService occurrenceService;

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(String.format("Checking if Teacher can access %s for action %s", context.getResourceType(), context.getAction()));
        return switch (accessContext.getActionType()) {
            case CREATE -> checkTeacherMarkAttendance(context, accessContext);
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

    private boolean checkTeacherMarkAttendance(PermissionContext context, ResourceAccessContext accessContext) {
        TeacherEntity teacher = context.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY);
        if(teacher == null)
            return false;
        Long scheduleOccurrenceId = accessContext.getAccessedMethodParameter("scheduleOccurrenceId");
        if(scheduleOccurrenceId == null)
            return false;
        ScheduleOccurrenceEntity occurrence = occurrenceService.fetchScheduleOccurrence(scheduleOccurrenceId);
        return occurrence.getSchedule().getTeacher().getId().equals(teacher.getId());
    }
}
