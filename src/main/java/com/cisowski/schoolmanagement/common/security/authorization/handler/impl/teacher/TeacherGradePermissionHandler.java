package com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.BaseResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.grade.model.grade.AddGradeRequest;
import com.cisowski.schoolmanagement.grade.model.grade.GradeEntity;
import com.cisowski.schoolmanagement.grade.model.grade.PatchGradeRequest;
import com.cisowski.schoolmanagement.grade.service.GradeService;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@RequiredArgsConstructor
public class TeacherGradePermissionHandler extends BaseResourcePermissionHandler<GradeEntity> {

    private final GradeService gradeService;

    @Override
    public UserType getSupportedUserType() {
        return UserType.TEACHER;
    }

    @Override
    public ResourceType getSupportedResourceType() {
        return ResourceType.GRADE;
    }

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        DbLogger.info(buildLogMessage(context.getAction().name()));
        return switch (context.getAction()) {
            case READ -> handleTeacherReads(context, accessContext);
            case CREATE -> handleTeacherCreate(context, accessContext);
            case UPDATE -> handleTeacherUpdate(context, accessContext);
            case DELETE -> handleTeacherDelete(context, accessContext);
        };
    }

    private boolean handleTeacherReads(PermissionContext context, ResourceAccessContext accessContext) {
        Long gradeId = accessContext.getAccessedMethodParameter("gradeId");
        if (gradeId != null)
            return handleTeacherGradeIdArg(context, gradeId);
        Integer studentId = accessContext.getAccessedMethodParameter("studentId");
        Integer subjectId = accessContext.getAccessedMethodParameter("subjectId");
        return handleTeacherStudentAndSubjectArgs(context, studentId, subjectId);
    }

    private boolean handleTeacherCreate(PermissionContext context, ResourceAccessContext accessContext) {
        AddGradeRequest request = accessContext.getAccessedMethodParameter("addGradeRequest");
        if (request != null)
            return isTeacherGradeOwnerAndSubjectTeacher(context, request.getTeacherId(), request.getSubjectId());
        return false;
    }

    private boolean handleTeacherUpdate(PermissionContext context, ResourceAccessContext accessContext) {
        PatchGradeRequest request = accessContext.getAccessedMethodParameter("patchGradeRequest");
        Long gradeId = accessContext.getAccessedMethodParameter("gradeId");
        GradeEntity grade = gradeService.fetchGrade(gradeId);
        return isTeacherGradeOwnerAndSubjectTeacher(context, grade.getTeacher().getId(), request.getSubjectId());
    }

    private boolean handleTeacherDelete(PermissionContext context, ResourceAccessContext accessContext) {
        Long gradeId = accessContext.getAccessedMethodParameter("gradeId");
        GradeEntity grade = gradeService.fetchGrade(gradeId);
        return isTeacherGradeOwnerAndSubjectTeacher(context, grade.getTeacher().getId(), grade.getSubject().getId());
    }

    private boolean handleTeacherGradeIdArg(PermissionContext context, Long gradeId) {
        GradeEntity grade = gradeService.fetchGrade(gradeId);
        TeacherEntity studentsHeadTeacher = grade.getStudent().getYearbook().getHeadTeacher();
        if (studentsHeadTeacher != null && studentsHeadTeacher.getId().equals(context.getUser().getId()))
            return true;
        return grade.getTeacher().getId().equals(context.getUser().getId());
    }

    private boolean isTeacherGradeOwnerAndSubjectTeacher(PermissionContext context, Integer requestTeacherId, Integer requestSubjectId) {
        boolean isGradeOwner = requestTeacherId.equals(context.getUser().getId());
        TeacherEntity teacher = context.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY);
        if(CollectionUtils.isEmpty(teacher.getTeachingSubjects()))
            return false;
        boolean isSubjectTeacher = teacher.getTeachingSubjects().stream()
                .anyMatch(subject -> subject.getId().equals(requestSubjectId));
        return isGradeOwner && isSubjectTeacher;
    }

    private boolean handleTeacherStudentAndSubjectArgs(PermissionContext context, Integer studentId, Integer subjectId) {
        TeacherEntity teacher = context.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY);
        if(teacher == null)
            return false;
        if(subjectId != null && studentId != null)
            return isTeacherTeachingSubject(teacher, subjectId) || isHeadTeacherOfStudent(teacher, studentId);
        else if (studentId != null)
            return isHeadTeacherOfStudent(teacher, studentId);
        else if (subjectId != null)
            return isTeacherTeachingSubject(teacher, subjectId);
        return false;
    }

    private boolean isHeadTeacherOfStudent(TeacherEntity teacher, Integer studentId){
        return teacher.getLeadingYearbook() != null && teacher.getLeadingYearbook().getStudentsInYearbook().stream()
                .anyMatch(student -> student.getId().equals(studentId));
    }

    private boolean isTeacherTeachingSubject(TeacherEntity teacher, Integer subjectId){
        return teacher.getTeachingSubjects().stream()
                .anyMatch(subject -> subject.getId().equals(subjectId));
    }
}
