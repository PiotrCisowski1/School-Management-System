package com.cisowski.schoolmanagement.unit.authorization.handler.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher.TeacherSubjectPermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
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
class TeacherSubjectPermissionHandlerTest {

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private TeacherSubjectPermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnTeacher() {
        UserType result = handler.getSupportedUserType();
        assertEquals(UserType.TEACHER, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnSubject() {
        ResourceType result = handler.getSupportedResourceType();
        assertEquals(ResourceType.SUBJECT, result);
    }

    @Test
    void canAccess_WhenActionIsReadAndSubjectIdProvidedAndTeacherTeachesSubject_ShouldReturnTrue() {
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setTeachingSubjects(Collections.singletonList(subject));
        Integer subjectId = subject.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(subjectId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndSubjectIdProvidedAndIsHeadTeacherOfYearbookTakingSubject_ShouldReturnTrue() {
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setMainCourseSubjects(Collections.singletonList(subject));
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setTeachingSubjects(Collections.emptyList());
        teacher.setLeadingYearbook(yearbook);
        Integer subjectId = subject.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(subjectId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndSubjectCodeProvidedAndTeacherTeachesSubject_ShouldReturnTrue() {
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setTeachingSubjects(Collections.singletonList(subject));

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("subjectCode")).thenReturn(subject.getCode());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndSubjectCodeProvidedAndIsHeadTeacherOfYearbookTakingSubject_ShouldReturnTrue() {
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setMainCourseSubjects(Collections.singletonList(subject));
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setLeadingYearbook(yearbook);
        teacher.setTeachingSubjects(Collections.emptyList());

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("subjectCode")).thenReturn(subject.getCode());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndNoParametersProvided_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("subjectCode")).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsNotRead_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.CREATE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenSubjectCodeEmpty_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("subjectCode")).thenReturn("");

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenSubjectCodeNull_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("subjectCode")).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenLeadingYearbookIsNullForSubjectId_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setTeachingSubjects(Collections.emptyList());
        teacher.setLeadingYearbook(null);
        Integer subjectId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(subjectId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenLeadingYearbookIsNullForSubjectCode_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        String subjectCode = "MATH101";

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("subjectCode")).thenReturn(subjectCode);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }
}