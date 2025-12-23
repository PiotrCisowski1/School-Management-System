package com.cisowski.schoolmanagement.unit.authorization.handler.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher.TeacherStudentPermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
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
class TeacherStudentPermissionHandlerTest {

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private TeacherStudentPermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnTeacher() {
        UserType result = handler.getSupportedUserType();
        assertEquals(UserType.TEACHER, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnStudent() {
        ResourceType result = handler.getSupportedResourceType();
        assertEquals(ResourceType.STUDENT, result);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherIsHeadTeacherOfStudent_ShouldReturnTrue() {
        StudentEntity student = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setStudentsInYearbook(Collections.singletonList(student));
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setLeadingYearbook(yearbook);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(student.getId());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherTeachesSubjectOfStudent_ShouldReturnTrue() {
        StudentEntity student = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setStudentsInYearbook(Collections.singletonList(student));
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        subject.setYearbooksTakingSubject(Collections.singletonList(yearbook));
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setLeadingYearbook(null);
        teacher.setTeachingSubjects(Collections.singletonList(subject));
        Integer studentId = student.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(studentId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherHasNoAccess_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setLeadingYearbook(null);
        teacher.setTeachingSubjects(Collections.emptyList());
        StudentEntity student = Instancio.create(StudentEntity.class);
        Integer studentId = student.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(studentId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherEntityIsNull_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndStudentIdIsNull_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsNotRead_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.UPDATE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenLeadingYearbookHasNoStudents_ShouldReturnFalse() {
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setStudentsInYearbook(Collections.emptyList());
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setLeadingYearbook(yearbook);
        Integer studentId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(studentId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenTeachingSubjectsHaveNoYearbooks_ShouldReturnFalse() {
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        subject.setYearbooksTakingSubject(Collections.emptyList());
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setLeadingYearbook(null);
        teacher.setTeachingSubjects(Collections.singletonList(subject));
        Integer studentId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(studentId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenYearbooksHaveNoStudents_ShouldReturnFalse() {
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setStudentsInYearbook(Collections.emptyList());
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        subject.setYearbooksTakingSubject(Collections.singletonList(yearbook));
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setLeadingYearbook(null);
        teacher.setTeachingSubjects(Collections.singletonList(subject));

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(123);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenHeadTeacherCheckFailsButSubjectTeacherCheckPasses_ShouldReturnTrue() {
        YearbookEntity leadingYearbook = Instancio.create(YearbookEntity.class);
        leadingYearbook.setStudentsInYearbook(Collections.emptyList());
        StudentEntity student = Instancio.create(StudentEntity.class);
        YearbookEntity teachingYearbook = Instancio.create(YearbookEntity.class);
        teachingYearbook.setStudentsInYearbook(Collections.singletonList(student));
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        subject.setYearbooksTakingSubject(Collections.singletonList(teachingYearbook));
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setLeadingYearbook(leadingYearbook);
        teacher.setTeachingSubjects(Collections.singletonList(subject));

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(student.getId());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }
}