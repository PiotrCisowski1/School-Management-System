package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.service.ScheduleOccurrenceService;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherOccurrencePermissionHandler extends BaseResourcePermissionHandler<ScheduleOccurrenceEntity> {

    private final ScheduleOccurrenceService occurrenceService;

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        if(accessContext.getActionType().equals(ResourceActionType.READ))
            return handleReads(context, accessContext);
        return false;
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.TEACHER;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.SCHEDULE_OCCURRENCE;
    }

    private boolean handleReads(PermissionContext context, ResourceAccessContext accessContext) {
        TeacherEntity teacher = context.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY);
        if(teacher == null)
            return false;

        return handleReadByYearbook(teacher, accessContext) ||
                handleSingleOccurrenceRead(teacher, accessContext);
    }

    private boolean handleReadByYearbook(TeacherEntity teacher, ResourceAccessContext accessContext) {
        Integer yearbookId = accessContext.getAccessedMethodParameter("yearbookId");
        if(yearbookId == null)
            return false;

        return teacher.getLeadingYearbook().getId().equals(yearbookId);
    }

    private boolean handleSingleOccurrenceRead(TeacherEntity teacher, ResourceAccessContext accessContext) {
        Long occurrenceId = accessContext.getAccessedMethodParameter("scheduleOccurrenceId");
        ScheduleOccurrenceEntity occurrence = occurrenceService.fetchScheduleOccurrence(occurrenceId);

        return occurrence.getSchedule().getTeacher().getId().equals(teacher.getId());
    }
}
