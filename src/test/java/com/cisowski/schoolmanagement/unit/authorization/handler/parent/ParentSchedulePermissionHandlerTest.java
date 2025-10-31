package com.cisowski.schoolmanagement.unit.authorization.handler.parent;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.parent.ParentSchedulePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.schedule.model.ScheduleEntity;
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
class ParentSchedulePermissionHandlerTest {

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private ParentSchedulePermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnParent() {
        UserType result = handler.getSupportedUserType();

        assertEquals(UserType.PARENT, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnSchedule() {
        ResourceType result = handler.getSupportedResourceType();

        assertEquals(ResourceType.SCHEDULE, result);
    }

    @Test
    void canAccess_WhenActionIsNotRead_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.CREATE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenParentEntityIsNull_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenScheduleVersionEntityIsNull_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenScheduleIdProvidedAndAllConditionsMet_ShouldReturnTrue() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        ScheduleVersionEntity scheduleVersion = Instancio.create(ScheduleVersionEntity.class);
        ScheduleEntity schedule = Instancio.create(ScheduleEntity.class);
        Integer scheduleId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleId")).thenReturn(scheduleId);

        parent.setChildren(List.of(child));
        child.setYearbook(yearbook);
        scheduleVersion.setYearbook(yearbook);
        scheduleVersion.setSchedules(List.of(schedule));
        schedule.setId(scheduleId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenScheduleIdProvidedButChildNotInYearbook_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        YearbookEntity childYearbook = Instancio.create(YearbookEntity.class);
        YearbookEntity scheduleVersionYearbook = Instancio.create(YearbookEntity.class);
        ScheduleVersionEntity scheduleVersion = Instancio.create(ScheduleVersionEntity.class);
        ScheduleEntity schedule = Instancio.create(ScheduleEntity.class);
        Integer scheduleId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleId")).thenReturn(scheduleId);

        parent.setChildren(List.of(child));
        child.setYearbook(childYearbook);
        scheduleVersion.setYearbook(scheduleVersionYearbook);
        scheduleVersion.setSchedules(List.of(schedule));
        schedule.setId(scheduleId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenScheduleIdProvidedButScheduleNotInVersion_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        ScheduleVersionEntity scheduleVersion = Instancio.create(ScheduleVersionEntity.class);
        ScheduleEntity schedule = Instancio.create(ScheduleEntity.class);
        Integer scheduleId = 123;
        Integer differentScheduleId = 456;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleId")).thenReturn(scheduleId);

        parent.setChildren(List.of(child));
        child.setYearbook(yearbook);
        scheduleVersion.setYearbook(yearbook);
        scheduleVersion.setSchedules(List.of(schedule));
        schedule.setId(differentScheduleId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenDayOfWeekProvidedAndChildInYearbook_ShouldReturnTrue() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        ScheduleVersionEntity scheduleVersion = Instancio.create(ScheduleVersionEntity.class);
        Integer dayOfWeek = 1;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("dayOfWeek")).thenReturn(dayOfWeek);

        parent.setChildren(List.of(child));
        child.setYearbook(yearbook);
        scheduleVersion.setYearbook(yearbook);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenDayOfWeekProvidedButChildNotInYearbook_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        YearbookEntity childYearbook = Instancio.create(YearbookEntity.class);
        YearbookEntity scheduleVersionYearbook = Instancio.create(YearbookEntity.class);
        ScheduleVersionEntity scheduleVersion = Instancio.create(ScheduleVersionEntity.class);
        Integer dayOfWeek = 1;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("dayOfWeek")).thenReturn(dayOfWeek);

        parent.setChildren(List.of(child));
        child.setYearbook(childYearbook);
        scheduleVersion.setYearbook(scheduleVersionYearbook);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenNoScheduleIdOrDayOfWeekProvided_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        ScheduleVersionEntity scheduleVersion = Instancio.create(ScheduleVersionEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("dayOfWeek")).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenParentHasNoChildren_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        ScheduleVersionEntity scheduleVersion = Instancio.create(ScheduleVersionEntity.class);
        Integer dayOfWeek = 1;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("dayOfWeek")).thenReturn(dayOfWeek);

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