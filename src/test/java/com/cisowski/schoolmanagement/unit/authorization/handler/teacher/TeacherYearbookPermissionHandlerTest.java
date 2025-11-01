package com.cisowski.schoolmanagement.unit.authorization.handler.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher.TeacherYearbookPermissionHandler;
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
class TeacherYearbookPermissionHandlerTest {

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private TeacherYearbookPermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnTeacher() {
        UserType result = handler.getSupportedUserType();
        assertEquals(UserType.TEACHER, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnYearbook() {
        ResourceType result = handler.getSupportedResourceType();
        assertEquals(ResourceType.YEARBOOK, result);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherIsLeadingYearbook_ShouldReturnTrue() {
        YearbookEntity leadingYearbook = Instancio.create(YearbookEntity.class);
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setLeadingYearbook(leadingYearbook);
        Integer yearbookId = leadingYearbook.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(yearbookId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherTeachesSubjectInYearbook_ShouldReturnTrue() {
        YearbookEntity leadingYearbook = Instancio.create(YearbookEntity.class);
        YearbookEntity teachingYearbook = Instancio.create(YearbookEntity.class);
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        subject.setYearbooksTakingSubject(Collections.singletonList(teachingYearbook));
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setLeadingYearbook(leadingYearbook);
        teacher.setTeachingSubjects(Collections.singletonList(subject));
        Integer yearbookId = teachingYearbook.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(yearbookId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndNoAccess_ShouldReturnFalse() {
        YearbookEntity leadingYearbook = Instancio.create(YearbookEntity.class);
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setLeadingYearbook(leadingYearbook);
        teacher.setTeachingSubjects(Collections.emptyList());
        YearbookEntity differentYearbook = Instancio.create(YearbookEntity.class);
        Integer yearbookId = differentYearbook.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(yearbookId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherEntityIsNull_ShouldReturnFalse() {
        Integer yearbookId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndYearbookIdIsNull_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(null);

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
    void canAccess_WhenLeadingYearbookIsNullAndNoTeachingSubjects_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setTeachingSubjects(Collections.emptyList());
        Integer yearbookId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(yearbookId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }
}