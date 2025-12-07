package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.student;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import org.springframework.stereotype.Component;

@Component
public class StudentSchedulePermissionHandler extends BaseResourcePermissionHandler<ScheduleEntity> {

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(buildLogMessage(context.getAction().name()));
        return switch (context.getAction()) {
            case READ -> handleSelfRead(context, accessContext);
            default -> false;
        };
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.STUDENT;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.SCHEDULE;
    }

    private boolean handleSelfRead(PermissionContext context, ResourceAccessContext accessContext) {
        StudentEntity accessingStudent = context.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY);
        if(accessingStudent == null)
            return false;
        ScheduleVersionEntity accessedScheduleVersion = context.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY);
        if (accessedScheduleVersion == null)
            return false;

        Integer scheduleId = accessContext.getAccessedMethodParameter("scheduleId");
        if(scheduleId != null)
            return isSchedulePartOfScheduleVersion(accessedScheduleVersion, scheduleId) && isSelfRead(accessingStudent, accessedScheduleVersion);

        return false;
    }

    private boolean isSelfRead(StudentEntity accessingStudent, ScheduleVersionEntity accessedScheduleVersion) {
        return accessingStudent.getYearbook().getId().equals(accessedScheduleVersion.getYearbook().getId());
    }

    private boolean isSchedulePartOfScheduleVersion(ScheduleVersionEntity scheduleVersion, Integer scheduleId) {
        return scheduleVersion.getSchedules().stream()
                .anyMatch(schedule -> schedule.getId().equals(scheduleId));
    }
}
