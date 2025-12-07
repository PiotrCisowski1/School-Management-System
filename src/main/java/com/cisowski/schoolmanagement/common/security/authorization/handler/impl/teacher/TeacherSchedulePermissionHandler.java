package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TeacherSchedulePermissionHandler extends BaseResourcePermissionHandler<ScheduleEntity> {

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(buildLogMessage(context.getAction().name()));
        this.requiresFiltering = false;
        return switch (context.getAction()) {
            case READ -> handleReads(context, accessContext);
            default -> false;
        };
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.TEACHER;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.SCHEDULE;
    }

    private boolean handleReads(PermissionContext context, ResourceAccessContext accessContext) {
        TeacherEntity accessingTeacher = context.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY);
        if(accessingTeacher == null)
            return false;
        ScheduleVersionEntity accessedScheduleVersion = context.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY);
        if (accessedScheduleVersion == null)
            return false;

        Integer accessedScheduleId = accessContext.getAccessedMethodParameter("scheduleId");
        if(accessedScheduleId != null) {
            return getScheduleEntityFromScheduleVersion(accessedScheduleVersion, accessedScheduleId)
                    .filter(scheduleEntity -> isTeacherTeachingSchedule(accessingTeacher, scheduleEntity) ||
                            isTeacherYearbookHeadTeacher(accessingTeacher, scheduleEntity))
                    .isPresent();
        }

        Integer scheduleByDayOfWeek = accessContext.getAccessedMethodParameter("dayOfWeek");
        if(scheduleByDayOfWeek != null)
            return isTeacherYearbookHeadTeacher(accessingTeacher, accessedScheduleVersion) ||
                    scheduleVersionContainsTeacherSchedules(accessingTeacher, accessedScheduleVersion);

        return false;
    }

    private Optional<ScheduleEntity> getScheduleEntityFromScheduleVersion(ScheduleVersionEntity scheduleVersion, Integer scheduleId) {
        return scheduleVersion.getSchedules().stream()
                .filter(schedule -> schedule.getId().equals(scheduleId))
                .findFirst();
    }

    private boolean isTeacherTeachingSchedule(TeacherEntity teacher, ScheduleEntity schedule) {
        return schedule.getTeacher().getId().equals(teacher.getId());
    }

    private boolean isTeacherYearbookHeadTeacher(TeacherEntity teacher, ScheduleEntity schedule) {
        return isTeacherYearbookHeadTeacher(teacher, schedule.getScheduleVersion());
    }

    private boolean isTeacherYearbookHeadTeacher(TeacherEntity teacher, ScheduleVersionEntity scheduleVersion) {
        return scheduleVersion.getYearbook().getHeadTeacher().getId().equals(teacher.getId());
    }

    private boolean scheduleVersionContainsTeacherSchedules(TeacherEntity teacher, ScheduleVersionEntity scheduleVersion) {
        if(scheduleVersion.getSchedules().stream()
                .anyMatch(schedule -> schedule.getTeacher().getId().equals(teacher.getId()))){
            this.requiresFiltering = true;
            return true;
        }
        return false;
    }
}
