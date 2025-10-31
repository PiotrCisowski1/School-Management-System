package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.student;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentSubjectPermissionHandler extends BaseResourcePermissionHandler<SubjectEntity> {
    @Override
    public UserType getSupportedUserType() {
        return UserType.STUDENT;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.SUBJECT;
    }

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        return switch (context.getAction()){
            case READ -> handleReads(context, accessContext);
            default -> false;
        };
    }

    private boolean handleReads(PermissionContext context, ResourceAccessContext accessContext){
        StudentEntity student = context.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY);
        if(student == null)
            return false;
        Integer subjectId = accessContext.getAccessedMethodParameter("subjectId");
        if(subjectId == null)
            return false;
        return student.getYearbook().getMainCourseSubjects().stream()
                .anyMatch(subject -> subject.getId().equals(subjectId));
    }
}
