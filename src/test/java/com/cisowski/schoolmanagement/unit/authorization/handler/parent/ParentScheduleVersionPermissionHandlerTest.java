package com.cisowski.schoolmanagement.unit.authorization.handler.parent;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.parent.ParentScheduleVersionPermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParentScheduleVersionPermissionHandlerTest {

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private ParentScheduleVersionPermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnParent() {
        UserType result = handler.getSupportedUserType();

        assertEquals(UserType.PARENT, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnScheduleVersion() {
        ResourceType result = handler.getSupportedResourceType();

        assertEquals(ResourceType.SCHEDULE_VERSION, result);
    }

    @Test
    void canAccess_WhenActionIsNotRead_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.CREATE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndParentEntityIsNull_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenYearbookIdProvidedAndIsChildsYearbook_ShouldReturnTrue() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        Integer yearbookId = yearbook.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(yearbookId);

        parent.setChildren(List.of(child));
        child.setYearbook(yearbook);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenYearbookIdProvidedAndIsNotChildsYearbook_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        YearbookEntity childYearbook = Instancio.create(YearbookEntity.class);
        YearbookEntity differentYearbook = Instancio.create(YearbookEntity.class);
        Integer yearbookId = differentYearbook.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(yearbookId);

        parent.setChildren(List.of(child));
        child.setYearbook(childYearbook);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenScheduleVersionIdProvidedAndIsChildsScheduleVersion_ShouldReturnTrue() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        ScheduleVersionEntity scheduleVersion = Instancio.create(ScheduleVersionEntity.class);
        Integer scheduleVersionId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleVersionId")).thenReturn(scheduleVersionId);

        parent.setChildren(List.of(child));
        child.setYearbook(yearbook);
        scheduleVersion.setYearbook(yearbook);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenScheduleVersionIdProvidedAndIsNotChildsScheduleVersion_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        YearbookEntity childYearbook = Instancio.create(YearbookEntity.class);
        YearbookEntity scheduleVersionYearbook = Instancio.create(YearbookEntity.class);
        ScheduleVersionEntity scheduleVersion = Instancio.create(ScheduleVersionEntity.class);
        Integer scheduleVersionId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleVersionId")).thenReturn(scheduleVersionId);

        parent.setChildren(List.of(child));
        child.setYearbook(childYearbook);
        scheduleVersion.setYearbook(scheduleVersionYearbook);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenScheduleVersionIdProvidedAndScheduleVersionEntityIsNull_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        Integer scheduleVersionId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleVersionId")).thenReturn(scheduleVersionId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenNoYearbookIdOrScheduleVersionIdProvided_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleVersionId")).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenYearbookIdTakesPrecedenceOverScheduleVersionId_ShouldUseYearbookId() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        Integer yearbookId = yearbook.getId();
        Integer scheduleVersionId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(yearbookId);

        parent.setChildren(List.of(child));
        child.setYearbook(yearbook);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
        verify(permissionContext, never()).getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY);
    }

    @Test
    void canAccess_WhenParentHasNoChildren_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        Integer yearbookId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(yearbookId);

        parent.setChildren(Collections.emptyList());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void requiresFiltering_ShouldReturnFalse() {
        boolean result = handler.requiresFiltering();

        assertFalse(result);
    }
}