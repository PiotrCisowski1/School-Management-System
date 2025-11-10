package com.cisowski.schoolmanagement.unit.authorization.handler.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher.TeacherClassroomPermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.schedule.service.ScheduleService;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
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
class TeacherClassroomPermissionHandlerTest {

    @Mock
    private ScheduleService scheduleService;

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private TeacherClassroomPermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnTeacher() {
        UserType result = handler.getSupportedUserType();
        assertEquals(UserType.TEACHER, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnClassroom() {
        ResourceType result = handler.getSupportedResourceType();
        assertEquals(ResourceType.CLASSROOM, result);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherHasAccessToClassroom_ShouldReturnTrue() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        ScheduleEntity schedule = mock(ScheduleEntity.class);
        Integer classroomId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getUser()).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("classroomId")).thenReturn(classroomId);
        when(scheduleService.fetchSchedulesByClassroomId(classroomId)).thenReturn(List.of(schedule));
        when(schedule.getTeacher()).thenReturn(teacher);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
        verify(scheduleService).fetchSchedulesByClassroomId(classroomId);
    }

    @Test
    void canAccess_WhenActionIsReadAndNoSchedulesFound_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        Integer classroomId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getUser()).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("classroomId")).thenReturn(classroomId);
        when(scheduleService.fetchSchedulesByClassroomId(classroomId)).thenReturn(Collections.emptyList());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
        verify(scheduleService).fetchSchedulesByClassroomId(classroomId);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherNotInSchedule_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        TeacherEntity differentTeacher = Instancio.create(TeacherEntity.class);
        ScheduleEntity schedule = Instancio.create(ScheduleEntity.class);
        schedule.setTeacher(differentTeacher);
        Integer classroomId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getUser()).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("classroomId")).thenReturn(classroomId);
        when(scheduleService.fetchSchedulesByClassroomId(classroomId)).thenReturn(List.of(schedule));

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
        verify(scheduleService).fetchSchedulesByClassroomId(classroomId);
    }

    @Test
    void canAccess_WhenActionIsReadAndClassroomIdIsNull_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(resourceAccessContext.getAccessedMethodParameter("classroomId")).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
        verify(scheduleService, never()).fetchSchedulesByClassroomId(anyInt());
    }

    @Test
    void canAccess_WhenActionIsNotRead_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.CREATE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenScheduleIsNull_ShouldNotThrowException() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        Integer classroomId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getUser()).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("classroomId")).thenReturn(classroomId);
        when(scheduleService.fetchSchedulesByClassroomId(classroomId)).thenReturn(Collections.emptyList());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
        verify(scheduleService).fetchSchedulesByClassroomId(classroomId);
    }
}