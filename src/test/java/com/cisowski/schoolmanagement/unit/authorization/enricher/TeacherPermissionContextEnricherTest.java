package com.cisowski.schoolmanagement.unit.authorization.enricher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.enricher.TeacherPermissionContextEnricher;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.timetable.schedule.service.ScheduleVersionService;
import com.cisowski.schoolmanagement.users.common.model.AuthorityEntity;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.service.TeacherService;
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
class TeacherPermissionContextEnricherTest {

    @Mock
    private ScheduleVersionService scheduleVersionService;

    @Mock
    private TeacherService teacherService;

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private TeacherPermissionContextEnricher enricher;

    @Test
    void supports_WhenUserHasTeacherAuthority_ShouldReturnTrue() {
        AuthorityEntity teacherAuthority = new AuthorityEntity();
        teacherAuthority.setAuthority(UserType.TEACHER.name());
        UserEntity user = Instancio.of(UserEntity.class)
                .set(field(UserEntity::getAuthority), Collections.singletonList(teacherAuthority))
                .create();

        boolean result = enricher.supports(user);

        assertTrue(result);
    }

    @Test
    void supports_WhenUserDoesNotHaveTeacherAuthority_ShouldReturnFalse() {
        AuthorityEntity authority = new AuthorityEntity();
        authority.setAuthority(UserType.STUDENT.name());
        UserEntity user = Instancio.of(UserEntity.class)
                .set(field(UserEntity::getAuthority), Collections.singletonList(authority))
                .create();

        boolean result = enricher.supports(user);

        assertFalse(result);
    }

    @Test
    void supports_WhenUserHasMultipleAuthoritiesIncludingTeacher_ShouldReturnTrue() {
        AuthorityEntity authority1 = new AuthorityEntity();
        authority1.setAuthority(UserType.STUDENT.name());
        AuthorityEntity authority2 = new AuthorityEntity();
        authority2.setAuthority(UserType.TEACHER.name());
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
    void enrich_ForScheduleResource_ShouldAddTeacherEntityAndScheduleVersion() {
        TeacherEntity user = Instancio.create(TeacherEntity.class);
        ScheduleVersionEntity scheduleVersionEntity = mock(ScheduleVersionEntity.class);
        Integer scheduleVersionId = 456;

        when(permissionContext.getUser()).thenReturn(user);
        when(permissionContext.getResourceType()).thenReturn(ResourceType.SCHEDULE);
        when(teacherService.fetchTeacher(user.getId())).thenReturn(user);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleVersionId")).thenReturn(scheduleVersionId);
        when(scheduleVersionService.fetchScheduleVersion(scheduleVersionId)).thenReturn(scheduleVersionEntity);

        enricher.enrich(permissionContext, resourceAccessContext);

        verify(permissionContext).putAttribute(PermissionContextAttributeKey.TEACHER_ENTITY, user);
        verify(permissionContext).putAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY, scheduleVersionEntity);
        verify(teacherService).fetchTeacher(user.getId());
        verify(scheduleVersionService).fetchScheduleVersion(scheduleVersionId);
    }

    @Test
    void enrich_ForNonScheduleResource_ShouldAddOnlyTeacherEntity() {
        TeacherEntity user = Instancio.create(TeacherEntity.class);

        when(permissionContext.getUser()).thenReturn(user);
        when(permissionContext.getResourceType()).thenReturn(ResourceType.GRADE);
        when(teacherService.fetchTeacher(user.getId())).thenReturn(user);

        enricher.enrich(permissionContext, resourceAccessContext);

        verify(permissionContext).putAttribute(PermissionContextAttributeKey.TEACHER_ENTITY, user);
        verify(permissionContext, never()).putAttribute(eq(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY), any());
        verify(teacherService).fetchTeacher(user.getId());
        verify(scheduleVersionService, never()).fetchScheduleVersion(anyInt());
    }

    @Test
    void enrich_ForScheduleVersionResource_ShouldAddOnlyTeacherEntity() {
        TeacherEntity user = Instancio.create(TeacherEntity.class);

        when(permissionContext.getUser()).thenReturn(user);
        when(permissionContext.getResourceType()).thenReturn(ResourceType.SCHEDULE_VERSION);
        when(teacherService.fetchTeacher(user.getId())).thenReturn(user);

        enricher.enrich(permissionContext, resourceAccessContext);

        verify(permissionContext).putAttribute(PermissionContextAttributeKey.TEACHER_ENTITY, user);
        verify(teacherService).fetchTeacher(user.getId());
        verify(scheduleVersionService, never()).fetchScheduleVersion(anyInt());
    }

    @Test
    void enrich_WhenScheduleVersionIdNotFound_ShouldHandleGracefully() {
        TeacherEntity user = Instancio.create(TeacherEntity.class);

        when(permissionContext.getUser()).thenReturn(user);
        when(permissionContext.getResourceType()).thenReturn(ResourceType.SCHEDULE);
        when(teacherService.fetchTeacher(user.getId())).thenReturn(user);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleVersionId")).thenReturn(null);

        enricher.enrich(permissionContext, resourceAccessContext);

        verify(permissionContext).putAttribute(PermissionContextAttributeKey.TEACHER_ENTITY, user);
        verify(scheduleVersionService, never()).fetchScheduleVersion(anyInt());
    }
}