package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherSubjectPermissionHandler extends BaseResourcePermissionHandler<SubjectEntity> {

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        return switch (context.getAction()){
            case READ -> handleReads(context, accessContext);
            case DELETE, CREATE, UPDATE -> false;
        };
    }

    @Override
    public UserType getSupportedUserType() {
        return UserType.TEACHER;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.SUBJECT;
    }

    private boolean handleReads(PermissionContext context, ResourceAccessContext accessContext){
        DbLogger.info(this.buildLogMessage(context.getAction().toString()));
        TeacherEntity teacher = context.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY);
        Integer subjectId = accessContext.getAccessedMethodParameter("subjectId");
        if(subjectId != null){
            return isTeachingSubject(teacher, subjectId) || isHeadTeacherOfYearbookTakingSubject(teacher, subjectId);
        }
        String subjectCode = accessContext.getAccessedMethodParameter("subjectCode");
        if(StringUtils.isNotEmpty(subjectCode))
            return isTeachingSubject(teacher, subjectCode) || isHeadTeacherOfYearbookTakingSubject(teacher, subjectCode);
        return false;
    }

    private boolean isTeachingSubject(TeacherEntity teacher, Integer subjectId){
        return teacher.getTeachingSubjects().stream()
                .anyMatch(subject -> subject.getId().equals(subjectId));
    }

    private boolean isHeadTeacherOfYearbookTakingSubject(TeacherEntity teacher, Integer subjectId){
        YearbookEntity managedYearbook = teacher.getLeadingYearbook();
        if(managedYearbook != null)
            return managedYearbook.getMainCourseSubjects().stream()
                    .anyMatch(subject -> subject.getId().equals(subjectId));
        return false;
    }

    private boolean isTeachingSubject(TeacherEntity teacher, String subjectCode){
        return teacher.getTeachingSubjects().stream()
                .anyMatch(subject -> subject.getCode().equals(subjectCode));
    }

    private boolean isHeadTeacherOfYearbookTakingSubject(TeacherEntity teacher, String subjectCode){
        YearbookEntity managedYearbook = teacher.getLeadingYearbook();
        if(managedYearbook != null)
            return managedYearbook.getMainCourseSubjects().stream()
                    .anyMatch(subject -> subject.getCode().equals(subjectCode));
        return false;
    }
}
