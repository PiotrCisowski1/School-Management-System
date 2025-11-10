package com.cisowski.schoolmanagement.unit.authorization.handler.student;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.student.StudentSchedulePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentSchedulePermissionHandlerTest {

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private StudentSchedulePermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnStudent() {
        UserType result = handler.getSupportedUserType();
        assertEquals(UserType.STUDENT, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnSchedule() {
        ResourceType result = handler.getSupportedResourceType();
        assertEquals(ResourceType.SCHEDULE, result);
    }

    @Test
    void canAccess_WhenActionIsReadAndScheduleIdProvidedAndMatches_ShouldReturnTrue() {
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        StudentEntity student = Instancio.create(StudentEntity.class);
        student.setYearbook(yearbook);
        ScheduleVersionEntity scheduleVersion = mock(ScheduleVersionEntity.class);
        ScheduleEntity schedule = mock(ScheduleEntity.class);
        Integer scheduleId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleId")).thenReturn(scheduleId);
        when(scheduleVersion.getSchedules()).thenReturn(List.of(schedule));
        when(schedule.getId()).thenReturn(scheduleId);
        when(scheduleVersion.getYearbook()).thenReturn(yearbook);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndScheduleIdProvidedButNotInVersion_ShouldReturnFalse() {
        StudentEntity student = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        ScheduleVersionEntity scheduleVersion = mock(ScheduleVersionEntity.class);
        ScheduleEntity schedule = mock(ScheduleEntity.class);
        Integer scheduleId = 123;
        Integer differentScheduleId = 456;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleId")).thenReturn(scheduleId);
        when(scheduleVersion.getSchedules()).thenReturn(List.of(schedule));
        when(schedule.getId()).thenReturn(differentScheduleId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndNoScheduleIdProvidedAndYearbooksDontMatch_ShouldReturnFalse() {
        StudentEntity student = Instancio.create(StudentEntity.class);
        YearbookEntity studentYearbook = Instancio.create(YearbookEntity.class);
        YearbookEntity scheduleVersionYearbook = Instancio.create(YearbookEntity.class);
        ScheduleVersionEntity scheduleVersion = mock(ScheduleVersionEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleId")).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndStudentEntityIsNull_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndScheduleVersionEntityIsNull_ShouldReturnFalse() {
        StudentEntity student = Instancio.create(StudentEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(null);

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