package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.student;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import org.springframework.stereotype.Component;

@Component
public class StudentScheduleVersionPermissionHandler extends BaseResourcePermissionHandler<ScheduleVersionEntity> {

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(buildLogMessage(context.getAction().name()));
        if(context.getAction().equals(ResourceActionType.READ))
            return handleSelfRead(context, accessContext);
        return false;
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.STUDENT;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.SCHEDULE_VERSION;
    }

    private boolean handleSelfRead(PermissionContext context, ResourceAccessContext accessContext) {
        StudentEntity student = context.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY);
        if(student == null)
            return false;

        Integer yearbookIdParam = accessContext.getAccessedMethodParameter("yearbookId");
        if(yearbookIdParam != null)
            return student.getYearbook().getId().equals(yearbookIdParam);

        Integer scheduleVersionIdParam = accessContext.getAccessedMethodParameter("scheduleVersionId");
        if(scheduleVersionIdParam != null) {
            ScheduleVersionEntity accessedScheduleVersion = context.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY);
            if(accessedScheduleVersion != null)
                return accessedScheduleVersion.getYearbook().getId().equals(student.getYearbook().getId());
        }

        return false;
    }
}
