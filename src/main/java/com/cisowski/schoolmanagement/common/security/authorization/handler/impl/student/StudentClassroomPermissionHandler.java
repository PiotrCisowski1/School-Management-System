package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.student;

import com.cisowski.schoolmanagement.classroom.model.ClassroomEntity;
import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StudentClassroomPermissionHandler extends BaseResourcePermissionHandler<ClassroomEntity> {

    private final ScheduleRepository scheduleRepository;

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        return switch (context.getAction()){
            case READ -> handleReadClassroom(context, accessContext);
            case CREATE, DELETE, UPDATE -> false;
        };
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.STUDENT;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.CLASSROOM;
    }

    private boolean handleReadClassroom(PermissionContext context, ResourceAccessContext accessContext){
        DbLogger.info(String.format("Checking if Student with ID %s can READ Classroom resource", context.getUser().getId()));
        Integer classroomId = accessContext.getAccessedMethodParameter("classroomId");
        List<ScheduleEntity> schedulesByClassroom = scheduleRepository.findByClassroomId(classroomId);
        if(!CollectionUtils.isEmpty(schedulesByClassroom)){
            return schedulesByClassroom.stream()
                    .anyMatch(schedule -> schedule.getSubject().getYearbooksTakingSubject().stream()
                            .anyMatch(yearbook -> yearbook.getStudentsInYearbook().stream()
                                    .anyMatch(student -> student.getId().equals(context.getUser().getId()))));
        }
        return false;
    }
}
