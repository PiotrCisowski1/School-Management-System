package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher;

import com.cisowski.schoolmanagement.classroom.model.ClassroomEntity;
import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TeacherClassroomPermissionHandler extends BaseResourcePermissionHandler<ClassroomEntity> {

    private final ScheduleService scheduleService;

    @Override
    public UserType getSupportedUserType() {
        return UserType.TEACHER;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.CLASSROOM;
    }

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(buildLogMessage(ResourceActionType.READ.name()));
        return switch (context.getAction()){
            case READ -> handleReads(context, accessContext);
            case DELETE, UPDATE, CREATE -> false;
        };
    }

    private boolean handleReads(PermissionContext context, ResourceAccessContext accessContext) {
        Integer classroomId = accessContext.getAccessedMethodParameter("classroomId");
        if(classroomId != null){
            List<ScheduleEntity> schedules = scheduleService.fetchSchedulesByClassroomId(classroomId);
            Integer authTeacherId = context.getUser().getId();
            return schedules.stream()
                    .filter(Objects::nonNull)
                    .anyMatch(schedule -> schedule.getTeacher().getId().equals(authTeacherId));
        }
        return false;
    }
}
