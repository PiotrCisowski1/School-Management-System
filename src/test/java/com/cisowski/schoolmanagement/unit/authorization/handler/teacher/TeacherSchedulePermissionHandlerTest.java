package com.cisowski.schoolmanagement.unit.authorization.handler.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher.TeacherSchedulePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
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
class TeacherSchedulePermissionHandlerTest {

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private TeacherSchedulePermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnTeacher() {
        UserType result = handler.getSupportedUserType();
        assertEquals(UserType.TEACHER, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnSchedule() {
        ResourceType result = handler.getSupportedResourceType();
        assertEquals(ResourceType.SCHEDULE, result);
    }

    @Test
    void canAccess_WhenActionIsReadAndScheduleIdProvidedAndTeacherIsTeachingSchedule_ShouldReturnTrue() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        ScheduleVersionEntity scheduleVersion = mock(ScheduleVersionEntity.class);
        ScheduleEntity schedule = mock(ScheduleEntity.class);
        Integer scheduleId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleId")).thenReturn(scheduleId);
        when(scheduleVersion.getSchedules()).thenReturn(List.of(schedule));
        when(schedule.getId()).thenReturn(scheduleId);
        when(schedule.getTeacher()).thenReturn(teacher);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
        assertFalse(handler.requiresFiltering());
    }

    @Test
    void canAccess_WhenActionIsReadAndScheduleIdProvidedAndTeacherIsHeadTeacher_ShouldReturnTrue() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        ScheduleEntity schedule = Instancio.create(ScheduleEntity.class);
        schedule.setTeacher(teacher);
        ScheduleVersionEntity scheduleVersion = Instancio.create(ScheduleVersionEntity.class);
        scheduleVersion.setSchedules(Collections.singletonList(schedule));
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setHeadTeacher(teacher);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleId")).thenReturn(schedule.getId());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
        assertFalse(handler.requiresFiltering());
    }

    @Test
    void canAccess_WhenActionIsReadAndScheduleIdProvidedAndNoAccess_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        TeacherEntity otherTeacher = Instancio.create(TeacherEntity.class);
        ScheduleEntity schedule = Instancio.create(ScheduleEntity.class);
        schedule.setTeacher(otherTeacher);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setHeadTeacher(otherTeacher);
        ScheduleVersionEntity scheduleVersion = Instancio.create(ScheduleVersionEntity.class);
        scheduleVersion.setSchedules(Collections.singletonList(schedule));
        scheduleVersion.setYearbook(yearbook);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleId")).thenReturn(schedule.getId());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
        assertFalse(handler.requiresFiltering());
    }

    @Test
    void canAccess_WhenActionIsReadAndDayOfWeekProvidedAndTeacherIsHeadTeacher_ShouldReturnTrue() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        ScheduleVersionEntity scheduleVersion = mock(ScheduleVersionEntity.class);
        YearbookEntity yearbook = mock(YearbookEntity.class);
        Integer dayOfWeek = 1;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("dayOfWeek")).thenReturn(dayOfWeek);
        when(scheduleVersion.getYearbook()).thenReturn(yearbook);
        when(yearbook.getHeadTeacher()).thenReturn(teacher);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
        assertFalse(handler.requiresFiltering());
    }

    @Test
    void canAccess_WhenActionIsReadAndDayOfWeekProvidedAndScheduleVersionContainsTeacherSchedules_ShouldReturnTrueAndSetRequiresFiltering() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        ScheduleVersionEntity scheduleVersion = mock(ScheduleVersionEntity.class);
        ScheduleEntity schedule = mock(ScheduleEntity.class);
        YearbookEntity yearbook = mock(YearbookEntity.class);
        Integer dayOfWeek = 1;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("dayOfWeek")).thenReturn(dayOfWeek);
        when(scheduleVersion.getYearbook()).thenReturn(yearbook);
        when(yearbook.getHeadTeacher()).thenReturn(mock(TeacherEntity.class));
        when(scheduleVersion.getSchedules()).thenReturn(List.of(schedule));
        when(schedule.getTeacher()).thenReturn(teacher);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
        assertTrue(handler.requiresFiltering());
    }

    @Test
    void canAccess_WhenActionIsReadAndDayOfWeekProvidedAndNoAccess_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        ScheduleVersionEntity scheduleVersion = mock(ScheduleVersionEntity.class);
        YearbookEntity yearbook = mock(YearbookEntity.class);
        Integer dayOfWeek = 1;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("dayOfWeek")).thenReturn(dayOfWeek);
        when(scheduleVersion.getYearbook()).thenReturn(yearbook);
        when(yearbook.getHeadTeacher()).thenReturn(mock(TeacherEntity.class));
        when(scheduleVersion.getSchedules()).thenReturn(List.of());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
        assertFalse(handler.requiresFiltering());
    }

    @Test
    void canAccess_WhenActionIsReadAndNoParametersProvided_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        ScheduleVersionEntity scheduleVersion = mock(ScheduleVersionEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("dayOfWeek")).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
        assertFalse(handler.requiresFiltering());
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherEntityIsNull_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
        assertFalse(handler.requiresFiltering());
    }

    @Test
    void canAccess_WhenActionIsReadAndScheduleVersionEntityIsNull_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
        assertFalse(handler.requiresFiltering());
    }

    @Test
    void canAccess_WhenActionIsNotRead_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.UPDATE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
        assertFalse(handler.requiresFiltering());
    }

    @Test
    void canAccess_WhenScheduleIdNotFoundInVersion_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        ScheduleVersionEntity scheduleVersion = mock(ScheduleVersionEntity.class);
        ScheduleEntity schedule = mock(ScheduleEntity.class);
        Integer scheduleId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleId")).thenReturn(scheduleId);
        when(scheduleVersion.getSchedules()).thenReturn(List.of(schedule));

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
        assertFalse(handler.requiresFiltering());
    }
}