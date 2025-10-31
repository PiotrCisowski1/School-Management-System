package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.parent;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.grade.model.grade.GradeEntity;
import com.cisowski.schoolmanagement.grade.service.GradeService;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParentGradePermissionHandler extends BaseResourcePermissionHandler<GradeEntity> {

    private final GradeService gradeService;

    @Override
    public UserType getSupportedUserType() {
        return UserType.PARENT;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.GRADE;
    }

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(buildLogMessage(ResourceActionType.READ.name()));
        if(context.getAction() != ResourceActionType.READ) return false;
        return handleParentReads(context, accessContext);
    }

    private boolean handleParentReads(PermissionContext context, ResourceAccessContext accessContext) {
        Integer studentId = accessContext.getAccessedMethodParameter("studentId");
        ParentEntity parent = context.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY);
        if(parent == null)
            return false;
        if(studentId != null){
            return isStudentParentChild(parent, studentId);
        }
        Long gradeId = accessContext.getAccessedMethodParameter("gradeId");
        if (gradeId != null){
            GradeEntity grade = gradeService.fetchGrade(gradeId);
            return isParentChildGradeOwner(parent, grade);
        }
        return false;
    }

    private boolean isStudentParentChild(ParentEntity parent, Integer studentId){
        return parent.getChildren().stream()
                .anyMatch(child -> child.getId().equals(studentId));
    }

    private boolean isParentChildGradeOwner(ParentEntity parent, GradeEntity grade){
        return parent.getChildren().stream()
                .anyMatch(child -> child.getId().equals(grade.getStudent().getId()));
    }
}
