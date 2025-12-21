package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.student;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.service.ScheduleOccurrenceService;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentOccurrencePermissionHandler extends BaseResourcePermissionHandler<ScheduleOccurrenceEntity> {

    private final ScheduleOccurrenceService occurrenceService;

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(String.format("Checking Student permission to %s on %s", context.getAction(), accessContext.getActionType()));
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
        return ResourceType.SCHEDULE_OCCURRENCE;
    }

    private boolean handleSelfRead(PermissionContext context, ResourceAccessContext accessContext) {
        StudentEntity student = context.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY);
        if(student == null)
            return false;

        return handleReadByYearbook(student, accessContext) ||
                handleSingleOccurrenceRead(student, accessContext);
    }

    private boolean handleReadByYearbook(StudentEntity student, ResourceAccessContext accessContext) {
        Integer yearbookId = accessContext.getAccessedMethodParameter("yearbookId");
        if(yearbookId == null)
            return false;

        return student.getYearbook().getId().equals(yearbookId);
    }

    private boolean handleSingleOccurrenceRead(StudentEntity student, ResourceAccessContext accessContext) {
        Long occurrenceId = accessContext.getAccessedMethodParameter("scheduleOccurrenceId");
        ScheduleOccurrenceEntity occurrence = occurrenceService.fetchScheduleOccurrence(occurrenceId);

        return occurrence.getSchedule().getScheduleVersion().getYearbook().getId().equals(student.getYearbook().getId());
    }
}
