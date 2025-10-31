package com.cisowski.schoolmanagement.unit.authorization.handler.student;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.student.StudentClassroomPermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.schedule.repository.ScheduleRepository;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
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
class StudentClassroomPermissionHandlerTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private StudentClassroomPermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnStudent() {
        UserType result = handler.getSupportedUserType();
        assertEquals(UserType.STUDENT, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnClassroom() {
        ResourceType result = handler.getSupportedResourceType();
        assertEquals(ResourceType.CLASSROOM, result);
    }

    @Test
    void canAccess_WhenActionIsReadAndStudentHasAccess_ShouldReturnTrue() {
        StudentEntity user = Instancio.create(StudentEntity.class);
        Integer classroomId = 123;
        ScheduleEntity schedule = mock(ScheduleEntity.class);
        SubjectEntity subject = mock(SubjectEntity.class);
        YearbookEntity yearbook = mock(YearbookEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getUser()).thenReturn(user);
        when(resourceAccessContext.getAccessedMethodParameter("classroomId")).thenReturn(classroomId);
        when(scheduleRepository.findByClassroomId(classroomId)).thenReturn(List.of(schedule));
        when(schedule.getSubject()).thenReturn(subject);
        when(subject.getYearbooksTakingSubject()).thenReturn(List.of(yearbook));
        when(yearbook.getStudentsInYearbook()).thenReturn(List.of(user));

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
        verify(scheduleRepository).findByClassroomId(classroomId);
    }

    @Test
    void canAccess_WhenActionIsReadAndNoSchedulesFound_ShouldReturnFalse() {
        Integer classroomId = 123;
        UserEntity user = Instancio.create(UserEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(resourceAccessContext.getAccessedMethodParameter("classroomId")).thenReturn(classroomId);
        when(scheduleRepository.findByClassroomId(classroomId)).thenReturn(Collections.emptyList());
        when(permissionContext.getUser()).thenReturn(user);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
        verify(scheduleRepository).findByClassroomId(classroomId);
    }

    @Test
    void canAccess_WhenActionIsReadAndStudentNotInYearbook_ShouldReturnFalse() {
        UserEntity user = Instancio.create(UserEntity.class);
        StudentEntity differentUser = Instancio.create(StudentEntity.class);
        Integer classroomId = 123;
        ScheduleEntity schedule = mock(ScheduleEntity.class);
        SubjectEntity subject = mock(SubjectEntity.class);
        YearbookEntity yearbook = mock(YearbookEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getUser()).thenReturn(user);
        when(resourceAccessContext.getAccessedMethodParameter("classroomId")).thenReturn(classroomId);
        when(scheduleRepository.findByClassroomId(classroomId)).thenReturn(List.of(schedule));
        when(schedule.getSubject()).thenReturn(subject);
        when(subject.getYearbooksTakingSubject()).thenReturn(List.of(yearbook));
        when(yearbook.getStudentsInYearbook()).thenReturn(List.of(differentUser));

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
        verify(scheduleRepository).findByClassroomId(classroomId);
    }

    @Test
    void canAccess_WhenActionIsCreate_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.CREATE);

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
    void canAccess_WhenActionIsDelete_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.DELETE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }
}