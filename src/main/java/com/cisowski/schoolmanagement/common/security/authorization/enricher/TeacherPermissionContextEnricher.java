package com.cisowski.schoolmanagement.common.security.authorization.enricher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.schedule.service.ScheduleVersionService;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.service.TeacherService;
import org.springframework.stereotype.Component;

@Component
public class TeacherPermissionContextEnricher extends BasePermissionContextEnricher {

    private final TeacherService teacherService;

    public TeacherPermissionContextEnricher(ScheduleVersionService scheduleVersionService, TeacherService teacherService) {
        super(scheduleVersionService);
        this.teacherService = teacherService;
    }

    @Override
    public void enrich(PermissionContext context, ResourceAccessContext accessContext) {
        Integer teacherId = context.getUser().getId();
        TeacherEntity teacher = teacherService.fetchTeacher(teacherId);
        context.putAttribute(PermissionContextAttributeKey.TEACHER_ENTITY, teacher);

        if (context.getResourceType().equals(ResourceType.SCHEDULE) ||
                context.getResourceType().equals(ResourceType.SCHEDULE_VERSION))
            addScheduleVersionToContext(context, accessContext);
    }

    @Override
    public boolean supports(UserEntity user) {
        return user.getAuthority().stream()
                .anyMatch(auth -> auth.getAuthority().equalsIgnoreCase(UserType.TEACHER.name()));
    }
}
