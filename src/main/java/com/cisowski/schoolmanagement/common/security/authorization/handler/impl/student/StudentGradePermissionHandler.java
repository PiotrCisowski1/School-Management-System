package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.student;

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
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentGradePermissionHandler extends BaseResourcePermissionHandler<GradeEntity> {

    private final GradeService gradeService;

    @Override
    public UserType getSupportedUserType() {
        return UserType.STUDENT;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.GRADE;
    }

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(buildLogMessage(ResourceActionType.READ.name()));
        if(context.getAction() != ResourceActionType.READ) return false;
        return handleStudentReads(context, accessContext);
    }

    private boolean handleStudentReads(PermissionContext context, ResourceAccessContext accessContext) {
        Long gradeId = accessContext.getAccessedMethodParameter("gradeId");
        if (gradeId != null) {
            GradeEntity grade = gradeService.fetchGrade(gradeId);
            return isStudentGradeOwner(context, grade);
        }
        Integer subjectId = accessContext.getAccessedMethodParameter("subjectId");
        Integer studentId = accessContext.getAccessedMethodParameter("studentId");
        if (subjectId != null && studentId != null)
            return isUserRequestStudent(context, studentId) && isUserSubjectAttendant(context, subjectId);
        else if (studentId != null)
            return isUserRequestStudent(context, studentId);
        return false;
    }

    private boolean isStudentGradeOwner(PermissionContext context, GradeEntity grade) {
        return grade.getStudent().getId().equals(context.getUser().getId());
    }

    private boolean isUserRequestStudent(PermissionContext context, Integer studentId) {
        return context.getUser().getId().equals(studentId);
    }

    private boolean isUserSubjectAttendant(PermissionContext context, Integer subjectId) {
        StudentEntity student = context.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY);
        if (student == null)
            return false;
        return student.getYearbook().getMainCourseSubjects().stream()
                .anyMatch(subject -> subject.getId().equals(subjectId));
    }
}
