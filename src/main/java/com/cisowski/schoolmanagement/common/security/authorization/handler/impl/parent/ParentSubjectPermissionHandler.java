package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.parent;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParentSubjectPermissionHandler extends BaseResourcePermissionHandler<SubjectEntity> {

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(buildLogMessage(context.getAction().toString()));
        return switch (context.getAction()){
            case READ -> handleReads(context, accessContext);
            default -> false;
        };
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.PARENT;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.SUBJECT;
    }

    private boolean handleReads(PermissionContext context, ResourceAccessContext accessContext) {
        ParentEntity parent = context.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY);
        if (parent == null)
            return false;
        Integer subjectId = accessContext.getAccessedMethodParameter("subjectId");
        if (subjectId == null)
            return false;
        return parent.getChildren().stream()
                .anyMatch(child -> child.getYearbook().getMainCourseSubjects().stream()
                        .anyMatch(subject -> subject.getId().equals(subjectId)));
    }
}
