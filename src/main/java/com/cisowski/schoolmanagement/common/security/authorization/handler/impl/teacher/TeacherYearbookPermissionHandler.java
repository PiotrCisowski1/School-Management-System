package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import org.springframework.stereotype.Component;

@Component
public class TeacherYearbookPermissionHandler extends BaseResourcePermissionHandler<YearbookEntity> {

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(buildLogMessage(context.getAction().name()));
        return switch (context.getAction()) {
            case READ -> handleReadingLeadingYearbookOrSubjectTeachingYearbook(context, accessContext);
            default -> false;
        };
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.TEACHER;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.YEARBOOK;
    }

    private boolean handleReadingLeadingYearbookOrSubjectTeachingYearbook(PermissionContext context, ResourceAccessContext accessContext) {
        TeacherEntity accessingTeacher = context.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY);
        if(accessingTeacher == null)
            return false;
        Integer accessedYearbookId = accessContext.getAccessedMethodParameter("yearbookId");
        if(accessedYearbookId == null)
            return false;
        if(accessingTeacher.getLeadingYearbook() != null)
            return isYearbooksHeadTeacher(accessingTeacher, accessedYearbookId) || isTeachingYearbooksSubject(accessingTeacher, accessedYearbookId);
        else
            return isTeachingYearbooksSubject(accessingTeacher, accessedYearbookId);
    }

    private boolean isYearbooksHeadTeacher(TeacherEntity teacher, Integer yearbookId) {
        return teacher.getLeadingYearbook().getId().equals(yearbookId);
    }

    private boolean isTeachingYearbooksSubject(TeacherEntity teacher, Integer yearbookId) {
        return teacher.getTeachingSubjects().stream()
                .anyMatch(subject -> subject.getYearbooksTakingSubject().stream()
                        .anyMatch(yearbook -> yearbook.getId().equals(yearbookId)));
    }
}
