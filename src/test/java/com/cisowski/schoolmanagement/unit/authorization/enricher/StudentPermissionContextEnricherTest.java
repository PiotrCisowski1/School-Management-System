package com.cisowski.schoolmanagement.unit.authorization.enricher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.enricher.StudentPermissionContextEnricher;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.schedule.service.ScheduleVersionService;
import com.cisowski.schoolmanagement.users.common.model.AuthorityEntity;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.student.service.StudentService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentPermissionContextEnricherTest {

    @Mock
    private ScheduleVersionService scheduleVersionService;

    @Mock
    private StudentService studentService;

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private StudentPermissionContextEnricher enricher;

    @Test
    void supports_WhenUserHasStudentAuthority_ShouldReturnTrue() {
        AuthorityEntity studentAuthority = new AuthorityEntity();
        studentAuthority.setAuthority(UserType.STUDENT.name());
        UserEntity user = Instancio.of(UserEntity.class)
                .set(field(UserEntity::getAuthority), Collections.singletonList(studentAuthority))
                .create();

        boolean result = enricher.supports(user);

        assertTrue(result);
    }

    @Test
    void supports_WhenUserDoesNotHaveStudentAuthority_ShouldReturnFalse() {
        AuthorityEntity authority = new AuthorityEntity();
        authority.setAuthority(UserType.TEACHER.name());
        UserEntity user = Instancio.of(UserEntity.class)
                .set(field(UserEntity::getAuthority), Collections.singletonList(authority))
                .create();

        boolean result = enricher.supports(user);

        assertFalse(result);
    }

    @Test
    void supports_WhenUserHasMultipleAuthoritiesIncludingStudent_ShouldReturnTrue() {
        AuthorityEntity authority1 = new AuthorityEntity();
        authority1.setAuthority(UserType.TEACHER.name());
        AuthorityEntity authority2 = new AuthorityEntity();
        authority2.setAuthority(UserType.STUDENT.name());
        UserEntity user = Instancio.of(UserEntity.class)
                .set(field(UserEntity::getAuthority), List.of(authority1, authority2))
                .create();

        boolean result = enricher.supports(user);

        assertTrue(result);
    }

    @Test
    void supports_WhenUserAuthoritiesEmpty_ShouldReturnFalse() {
        UserEntity user = Instancio.create(UserEntity.class);
        user.setAuthority(Collections.emptyList());

        boolean result = enricher.supports(user);

        assertFalse(result);
    }

    @Test
    void enrich_ForScheduleResource_ShouldAddStudentEntityAndScheduleVersion() {
        StudentEntity user = Instancio.create(StudentEntity.class);
        ScheduleVersionEntity scheduleVersionEntity = mock(ScheduleVersionEntity.class);
        Integer scheduleVersionId = 456;

        when(permissionContext.getUser()).thenReturn(user);
        when(permissionContext.getResourceType()).thenReturn(ResourceType.SCHEDULE);
        when(studentService.fetchStudent(user.getId())).thenReturn(user);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleVersionId")).thenReturn(scheduleVersionId);
        when(scheduleVersionService.fetchScheduleVersion(scheduleVersionId)).thenReturn(scheduleVersionEntity);

        enricher.enrich(permissionContext, resourceAccessContext);

        verify(permissionContext).putAttribute(PermissionContextAttributeKey.STUDENT_ENTITY, user);
        verify(permissionContext).putAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY, scheduleVersionEntity);
        verify(studentService).fetchStudent(user.getId());
        verify(scheduleVersionService).fetchScheduleVersion(scheduleVersionId);
    }

    @Test
    void enrich_ForNonScheduleResource_ShouldAddOnlyStudentEntity() {
        StudentEntity user = Instancio.create(StudentEntity.class);

        when(permissionContext.getUser()).thenReturn(user);
        when(permissionContext.getResourceType()).thenReturn(ResourceType.GRADE);
        when(studentService.fetchStudent(user.getId())).thenReturn(user);

        enricher.enrich(permissionContext, resourceAccessContext);

        verify(permissionContext).putAttribute(PermissionContextAttributeKey.STUDENT_ENTITY, user);
        verify(permissionContext, never()).putAttribute(eq(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY), any());
        verify(studentService).fetchStudent(user.getId());
        verify(scheduleVersionService, never()).fetchScheduleVersion(anyInt());
    }

    @Test
    void enrich_ForScheduleVersionResource_ShouldAddOnlyStudentEntity() {
        StudentEntity user = Instancio.create(StudentEntity.class);

        when(permissionContext.getUser()).thenReturn(user);
        when(permissionContext.getResourceType()).thenReturn(ResourceType.SCHEDULE_VERSION);
        when(studentService.fetchStudent(user.getId())).thenReturn(user);

        enricher.enrich(permissionContext, resourceAccessContext);

        verify(permissionContext).putAttribute(PermissionContextAttributeKey.STUDENT_ENTITY, user);
        verify(studentService).fetchStudent(user.getId());
        verify(scheduleVersionService, never()).fetchScheduleVersion(anyInt());
    }

    @Test
    void enrich_WhenScheduleVersionIdNotFound_ShouldHandleGracefully() {
        StudentEntity user = Instancio.create(StudentEntity.class);

        when(permissionContext.getUser()).thenReturn(user);
        when(permissionContext.getResourceType()).thenReturn(ResourceType.SCHEDULE);
        when(studentService.fetchStudent(user.getId())).thenReturn(user);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleVersionId")).thenReturn(null);

        enricher.enrich(permissionContext, resourceAccessContext);

        verify(permissionContext).putAttribute(PermissionContextAttributeKey.STUDENT_ENTITY, user);
        verify(scheduleVersionService, never()).fetchScheduleVersion(anyInt());
    }
}