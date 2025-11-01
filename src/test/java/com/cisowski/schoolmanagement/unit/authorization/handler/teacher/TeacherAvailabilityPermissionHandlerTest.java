package com.cisowski.schoolmanagement.unit.authorization.handler.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher.TeacherAvailabilityPermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherAvailabilityPermissionHandlerTest {

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private TeacherAvailabilityPermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnTeacher() {
        UserType result = handler.getSupportedUserType();
        assertEquals(UserType.TEACHER, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnTeacherAvailability() {
        ResourceType result = handler.getSupportedResourceType();
        assertEquals(ResourceType.TEACHER_AVAILABILITY, result);
    }

    @Test
    void canAccess_WhenActionIsCreateAndSameTeacher_ShouldReturnTrue() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        Integer teacherId = teacher.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.CREATE);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("teacherId")).thenReturn(teacherId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
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
    void canAccess_WhenActionIsDeleteAndSameTeacher_ShouldReturnTrue() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        Integer teacherId = teacher.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.DELETE);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("teacherId")).thenReturn(teacherId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsCreateAndDifferentTeacher_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        Integer differentTeacherId = teacher.getId() + 1;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.CREATE);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("teacherId")).thenReturn(differentTeacherId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherEntityIsNull_ShouldReturnFalse() {
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
    void canAccess_WhenActionIsUpdate_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.UPDATE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsWrite_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.UPDATE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }
}