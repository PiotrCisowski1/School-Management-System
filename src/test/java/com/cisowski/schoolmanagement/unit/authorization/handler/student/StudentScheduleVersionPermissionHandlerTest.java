package com.cisowski.schoolmanagement.unit.authorization.handler.student;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.student.StudentScheduleVersionPermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
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
class StudentScheduleVersionPermissionHandlerTest {

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private StudentScheduleVersionPermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnStudent() {
        UserType result = handler.getSupportedUserType();
        assertEquals(UserType.STUDENT, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnScheduleVersion() {
        ResourceType result = handler.getSupportedResourceType();
        assertEquals(ResourceType.SCHEDULE_VERSION, result);
    }

    @Test
    void canAccess_WhenActionIsNotRead_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.UPDATE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenYearbookIdProvidedAndMatchesStudentYearbook_ShouldReturnTrue() {
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
    void canAccess_WhenYearbookIdProvidedAndDoesNotMatchStudentYearbook_ShouldReturnFalse() {
        StudentEntity student = Instancio.create(StudentEntity.class);
        YearbookEntity studentYearbook = Instancio.create(YearbookEntity.class);
        YearbookEntity differentYearbook = Instancio.create(YearbookEntity.class);
        Integer yearbookId = differentYearbook.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(yearbookId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenScheduleVersionIdProvidedAndYearbooksMatch_ShouldReturnTrue() {
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        StudentEntity student = Instancio.create(StudentEntity.class);
        student.setYearbook(yearbook);
        ScheduleVersionEntity scheduleVersion = mock(ScheduleVersionEntity.class);
        Integer scheduleVersionId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleVersionId")).thenReturn(scheduleVersionId);
        when(scheduleVersion.getYearbook()).thenReturn(yearbook);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenScheduleVersionIdProvidedAndYearbooksDontMatch_ShouldReturnFalse() {
        StudentEntity student = Instancio.create(StudentEntity.class);
        YearbookEntity studentYearbook = Instancio.create(YearbookEntity.class);
        YearbookEntity scheduleVersionYearbook = Instancio.create(YearbookEntity.class);
        ScheduleVersionEntity scheduleVersion = mock(ScheduleVersionEntity.class);
        Integer scheduleVersionId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(scheduleVersion);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleVersionId")).thenReturn(scheduleVersionId);
        when(scheduleVersion.getYearbook()).thenReturn(scheduleVersionYearbook);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenScheduleVersionIdProvidedButScheduleVersionEntityIsNull_ShouldReturnFalse() {
        StudentEntity student = Instancio.create(StudentEntity.class);
        Integer scheduleVersionId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY)).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleVersionId")).thenReturn(scheduleVersionId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenStudentEntityIsNull_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenNoParametersProvided_ShouldReturnFalse() {
        StudentEntity student = Instancio.create(StudentEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleVersionId")).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenYearbookIdTakesPrecedence_ShouldUseYearbookId() {
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        StudentEntity student = Instancio.create(StudentEntity.class);
        student.setYearbook(yearbook);
        Integer yearbookId = yearbook.getId();
        Integer scheduleVersionId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(yearbookId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
        verify(permissionContext, never()).getAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY);
    }
}