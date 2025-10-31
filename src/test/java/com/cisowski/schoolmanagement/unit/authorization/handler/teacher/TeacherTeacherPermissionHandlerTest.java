package com.cisowski.schoolmanagement.unit.authorization.handler.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher.TeacherTeacherPermissionHandler;
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
class TeacherTeacherPermissionHandlerTest {

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private TeacherTeacherPermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnTeacher() {
        UserType result = handler.getSupportedUserType();
        assertEquals(UserType.TEACHER, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnTeacher() {
        ResourceType result = handler.getSupportedResourceType();
        assertEquals(ResourceType.TEACHER, result);
    }

    @Test
    void canAccess_WhenActionIsReadAndSameTeacher_ShouldReturnTrue() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        Integer teacherId = teacher.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("teacherId")).thenReturn(teacherId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndCorelatedWithSameYearbook_ShouldReturnTrue() {
        TeacherEntity otherTeacher = Instancio.create(TeacherEntity.class);
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        subject.setTeachers(Collections.singletonList(otherTeacher));
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setMainCourseSubjects(Collections.singletonList(subject));
        TeacherEntity accessingTeacher = Instancio.create(TeacherEntity.class);
        accessingTeacher.setLeadingYearbook(yearbook);
        Integer teacherId = otherTeacher.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(accessingTeacher);
        when(resourceAccessContext.getAccessedMethodParameter("teacherId")).thenReturn(teacherId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndNotCorelated_ShouldReturnFalse() {
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        subject.setTeachers(Collections.emptyList());
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setMainCourseSubjects(Collections.singletonList(subject));
        TeacherEntity accessingTeacher = Instancio.create(TeacherEntity.class);
        accessingTeacher.setLeadingYearbook(yearbook);
        TeacherEntity otherTeacher = Instancio.create(TeacherEntity.class);
        Integer teacherId = otherTeacher.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(accessingTeacher);
        when(resourceAccessContext.getAccessedMethodParameter("teacherId")).thenReturn(teacherId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherEntityIsNull_ShouldReturnFalse() {
        Integer teacherId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherIdIsNull_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("teacherId")).thenReturn(null);

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
    void canAccess_WhenLeadingYearbookIsNull_ShouldReturnFalse() {
        TeacherEntity accessingTeacher = Instancio.create(TeacherEntity.class);
        accessingTeacher.setLeadingYearbook(null);
        TeacherEntity otherTeacher = Instancio.create(TeacherEntity.class);
        Integer teacherId = otherTeacher.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(accessingTeacher);
        when(resourceAccessContext.getAccessedMethodParameter("teacherId")).thenReturn(teacherId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenLeadingYearbookHasNoSubjects_ShouldReturnFalse() {
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setMainCourseSubjects(Collections.emptyList());
        TeacherEntity accessingTeacher = Instancio.create(TeacherEntity.class);
        accessingTeacher.setLeadingYearbook(yearbook);
        TeacherEntity otherTeacher = Instancio.create(TeacherEntity.class);
        Integer teacherId = otherTeacher.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(accessingTeacher);
        when(resourceAccessContext.getAccessedMethodParameter("teacherId")).thenReturn(teacherId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }
}