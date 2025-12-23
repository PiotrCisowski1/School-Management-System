package com.cisowski.schoolmanagement.unit.authorization.handler.student;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.student.StudentYearbookPermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentYearbookPermissionHandlerTest {

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private StudentYearbookPermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnStudent() {
        UserType result = handler.getSupportedUserType();
        assertEquals(UserType.STUDENT, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnYearbook() {
        ResourceType result = handler.getSupportedResourceType();
        assertEquals(ResourceType.YEARBOOK, result);
    }

    @Test
    void canAccess_WhenActionIsReadAndYearbookIdMatches_ShouldReturnTrue() {
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        StudentEntity student = Instancio.create(StudentEntity.class);
        student.setYearbook(yearbook);
        Integer yearbookId = yearbook.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(yearbookId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndYearbookIdDoesNotMatch_ShouldReturnFalse() {
        YearbookEntity studentYearbook = Instancio.create(YearbookEntity.class);
        StudentEntity student = Instancio.create(StudentEntity.class);
        student.setYearbook(studentYearbook);
        YearbookEntity differentYearbook = Instancio.create(YearbookEntity.class);
        Integer yearbookId = differentYearbook.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(yearbookId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndStudentEntityIsNull_ShouldReturnFalse() {
        Integer yearbookId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndYearbookIdIsNull_ShouldReturnFalse() {
        StudentEntity student = Instancio.create(StudentEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsWrite_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.UPDATE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsDelete_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.DELETE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsCreate_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.CREATE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }
}