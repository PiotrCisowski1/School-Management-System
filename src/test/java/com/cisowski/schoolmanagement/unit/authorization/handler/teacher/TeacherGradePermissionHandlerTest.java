package com.cisowski.schoolmanagement.unit.authorization.handler.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher.TeacherGradePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.grade.model.grade.AddGradeRequest;
import com.cisowski.schoolmanagement.grade.model.grade.GradeEntity;
import com.cisowski.schoolmanagement.grade.model.grade.PatchGradeRequest;
import com.cisowski.schoolmanagement.grade.service.GradeService;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherGradePermissionHandlerTest {

    @Mock
    private GradeService gradeService;

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private TeacherGradePermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnTeacher() {
        UserType result = handler.getSupportedUserType();
        assertEquals(UserType.TEACHER, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnGrade() {
        ResourceType result = handler.getSupportedResourceType();
        assertEquals(ResourceType.GRADE, result);
    }

    @Test
    void canAccess_WhenActionIsReadAndGradeIdProvidedAndTeacherIsGradeOwner_ShouldReturnTrue() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        StudentEntity student = Instancio.create(StudentEntity.class);
        GradeEntity grade = Instancio.create(GradeEntity.class);
        grade.setTeacher(teacher);
        grade.setStudent(student);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getUser()).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(grade.getId());
        when(gradeService.fetchGrade(grade.getId())).thenReturn(grade);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
        verify(gradeService).fetchGrade(grade.getId());
    }

    @Test
    void canAccess_WhenActionIsReadAndGradeIdProvidedAndTeacherIsHeadTeacher_ShouldReturnTrue() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setHeadTeacher(teacher);
        StudentEntity student = Instancio.create(StudentEntity.class);
        student.setYearbook(yearbook);
        GradeEntity grade = Instancio.create(GradeEntity.class);
        grade.setTeacher(teacher);
        grade.setStudent(student);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getUser()).thenReturn(teacher);
        when(gradeService.fetchGrade(grade.getId())).thenReturn(grade);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(grade.getId());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
        verify(gradeService).fetchGrade(grade.getId());
    }

    @Test
    void canAccess_WhenActionIsCreateAndValidRequest_ShouldReturnTrue() {
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setTeachingSubjects(Collections.singletonList(subject));
        AddGradeRequest request = mock(AddGradeRequest.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.CREATE);
        when(permissionContext.getUser()).thenReturn(teacher);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("addGradeRequest")).thenReturn(request);
        when(request.getTeacherId()).thenReturn(teacher.getId());
        when(request.getSubjectId()).thenReturn(subject.getId());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsUpdateAndValidRequest_ShouldReturnTrue() {
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setTeachingSubjects(Collections.singletonList(subject));
        PatchGradeRequest request = mock(PatchGradeRequest.class);
        GradeEntity grade = Instancio.create(GradeEntity.class);
        grade.setTeacher(teacher);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.UPDATE);
        when(permissionContext.getUser()).thenReturn(teacher);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("patchGradeRequest")).thenReturn(request);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(grade.getId());
        when(gradeService.fetchGrade(grade.getId())).thenReturn(grade);
        when(request.getSubjectId()).thenReturn(subject.getId());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
        verify(gradeService).fetchGrade(grade.getId());
    }

    @Test
    void canAccess_WhenActionIsDeleteAndValidRequest_ShouldReturnTrue() {
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setTeachingSubjects(Collections.singletonList(subject));
        GradeEntity grade = Instancio.create(GradeEntity.class);
        grade.setTeacher(teacher);
        grade.setSubject(subject);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.DELETE);
        when(permissionContext.getUser()).thenReturn(teacher);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(grade.getId());
        when(gradeService.fetchGrade(grade.getId())).thenReturn(grade);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
        verify(gradeService).fetchGrade(grade.getId());
    }

    @Test
    void canAccess_WhenActionIsReadAndStudentIdAndSubjectIdProvided_ShouldReturnTrue() {
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setTeachingSubjects(Collections.singletonList(subject));
        Integer studentId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(studentId);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(subject.getId());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndOnlyStudentIdProvidedAndIsHeadTeacher_ShouldReturnTrue() {
        StudentEntity student = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setStudentsInYearbook(Collections.singletonList(student));
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setLeadingYearbook(yearbook);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(student.getId());
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndOnlySubjectIdProvidedAndTeacherTeachesSubject_ShouldReturnTrue() {
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setTeachingSubjects(Collections.singletonList(subject));

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(subject.getId());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsCreateAndNotGradeOwner_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        AddGradeRequest request = mock(AddGradeRequest.class);
        Integer differentTeacherId = teacher.getId() + 1;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.CREATE);
        when(permissionContext.getUser()).thenReturn(teacher);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("addGradeRequest")).thenReturn(request);
        when(request.getTeacherId()).thenReturn(differentTeacherId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsCreateAndNoTeachingSubjects_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setTeachingSubjects(Collections.emptyList());
        AddGradeRequest request = mock(AddGradeRequest.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.CREATE);
        when(permissionContext.getUser()).thenReturn(teacher);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("addGradeRequest")).thenReturn(request);
        when(request.getTeacherId()).thenReturn(teacher.getId());
        when(request.getSubjectId()).thenReturn(123);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndNoParametersProvided_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }
}