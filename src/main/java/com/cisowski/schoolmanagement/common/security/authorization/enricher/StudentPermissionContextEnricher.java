package com.cisowski.schoolmanagement.common.security.authorization.enricher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.schedule.service.ScheduleVersionService;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.student.service.StudentService;
import org.springframework.stereotype.Component;

@Component
public class StudentPermissionContextEnricher extends BasePermissionContextEnricher {

    private final StudentService studentService;

    public StudentPermissionContextEnricher(ScheduleVersionService scheduleVersionService, StudentService studentService) {
        super(scheduleVersionService);
        this.studentService = studentService;
    }

    @Override
    public void enrich(PermissionContext context, ResourceAccessContext accessContext) {
        Integer studentId = context.getUser().getId();
        StudentEntity student = studentService.fetchStudent(studentId);
        context.putAttribute(PermissionContextAttributeKey.STUDENT_ENTITY, student);

        if (context.getResourceType().equals(ResourceType.SCHEDULE) ||
                context.getResourceType().equals(ResourceType.SCHEDULE_VERSION))
            addScheduleVersionToContext(context, accessContext);
    }

    @Override
    public boolean supports(UserEntity user) {
        return user.getAuthority().stream()
                .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase(UserType.STUDENT.name()));
    }
}
