package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import org.springframework.stereotype.Component;

@Component
public class TeacherScheduleVersionPermissionHandler extends BaseResourcePermissionHandler<ScheduleVersionEntity> {

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(buildLogMessage(context.getAction().name()));
        if(context.getAction().equals(ResourceActionType.READ))
            return handleReads(context, accessContext);
        return false;
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.TEACHER;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.SCHEDULE_VERSION;
    }

    private boolean handleReads(PermissionContext context, ResourceAccessContext accessContext) {
        TeacherEntity accessingTeacher = context.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY);
        if(accessingTeacher == null)
            return false;

        Integer yearbookIdParam = accessContext.getAccessedMethodParameter("yearbookId");
        if(yearbookIdParam != null)
            return isHeadTeacherOfYearbook(accessingTeacher, yearbookIdParam);

        Integer scheduleVersionIdParam = accessContext.getAccessedMethodParameter("scheduleVersionId");
        if(scheduleVersionIdParam != null){
            ScheduleVersionEntity accessedScheduleVersion = context.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY);
            if(accessedScheduleVersion != null)
                return isHeadTeacherOfYearbook(accessingTeacher, accessedScheduleVersion.getYearbook().getId());
        }

        return false;
    }

    private boolean isHeadTeacherOfYearbook(TeacherEntity teacher, Integer yearbookId) {
        if(teacher.getLeadingYearbook() != null)
            return teacher.getLeadingYearbook().getId().equals(yearbookId);
        return false;
    }
}