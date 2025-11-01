package com.cisowski.schoolmanagement.unit.authorization.enricher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.enricher.ParentPermissionContextEnricher;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.schedule.service.ScheduleVersionService;
import com.cisowski.schoolmanagement.users.common.model.AuthorityEntity;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.parent.service.ParentService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParentPermissionContextEnricherTest {

    @Mock
    private ScheduleVersionService scheduleVersionService;

    @Mock
    private ParentService parentService;

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private ParentPermissionContextEnricher enricher;

    @Test
    void supports_WhenUserHasParentAuthority_ShouldReturnTrue() {
        AuthorityEntity parentAuthority = new AuthorityEntity();
        parentAuthority.setAuthority(UserType.PARENT.name());
        UserEntity user = Instancio.of(ParentEntity.class)
                .set(field(UserEntity::getAuthority), Collections.singletonList(parentAuthority))
                .create();

        boolean result = enricher.supports(user);

        assertTrue(result);
    }

    @Test
    void supports_WhenUserDoesNotHaveParentAuthority_ShouldReturnFalse() {
        AuthorityEntity authority = new AuthorityEntity();
        authority.setAuthority(UserType.TEACHER.name());
        UserEntity user = Instancio.of(ParentEntity.class)
                .set(field(UserEntity::getAuthority), Collections.singletonList(authority))
                .create();

        boolean result = enricher.supports(user);

        assertFalse(result);
    }

    @Test
    void supports_WhenUserHasMultipleAuthoritiesIncludingParent_ShouldReturnTrue() {
        AuthorityEntity authority = new AuthorityEntity();
        authority.setAuthority(UserType.TEACHER.name());
        AuthorityEntity authority2 = new AuthorityEntity();
        authority.setAuthority(UserType.PARENT.name());
        UserEntity user = Instancio.of(ParentEntity.class)
                .set(field(UserEntity::getAuthority), List.of(authority, authority2))
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
    void enrich_ForScheduleResource_ShouldAddParentEntityAndScheduleVersion() {
        ParentEntity user = Instancio.create(ParentEntity.class);
        ScheduleVersionEntity scheduleVersionEntity = mock(ScheduleVersionEntity.class);
        Integer scheduleVersionId = 456;

        when(permissionContext.getUser()).thenReturn(user);
        when(permissionContext.getResourceType()).thenReturn(ResourceType.SCHEDULE);
        when(parentService.fetchParentEntity(user.getId())).thenReturn(user);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleVersionId")).thenReturn(scheduleVersionId);
        when(scheduleVersionService.fetchScheduleVersion(scheduleVersionId)).thenReturn(scheduleVersionEntity);

        enricher.enrich(permissionContext, resourceAccessContext);

        verify(permissionContext).putAttribute(PermissionContextAttributeKey.PARENT_ENTITY, user);
        verify(permissionContext).putAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY, scheduleVersionEntity);
        verify(parentService).fetchParentEntity(user.getId());
        verify(scheduleVersionService).fetchScheduleVersion(scheduleVersionId);
    }

    @Test
    void enrich_ForScheduleVersionResource_ShouldAddParentEntityAndScheduleVersion() {
        ParentEntity user = Instancio.create(ParentEntity.class);
        ScheduleVersionEntity scheduleVersionEntity = mock(ScheduleVersionEntity.class);
        Integer scheduleVersionId = 456;

        when(permissionContext.getUser()).thenReturn(user);
        when(permissionContext.getResourceType()).thenReturn(ResourceType.SCHEDULE_VERSION);
        when(parentService.fetchParentEntity(user.getId())).thenReturn(user);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleVersionId")).thenReturn(scheduleVersionId);
        when(scheduleVersionService.fetchScheduleVersion(scheduleVersionId)).thenReturn(scheduleVersionEntity);

        enricher.enrich(permissionContext, resourceAccessContext);

        verify(permissionContext).putAttribute(PermissionContextAttributeKey.PARENT_ENTITY, user);
        verify(permissionContext).putAttribute(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY, scheduleVersionEntity);
        verify(parentService).fetchParentEntity(user.getId());
        verify(scheduleVersionService).fetchScheduleVersion(scheduleVersionId);
    }

    @Test
    void enrich_ForNonScheduleResource_ShouldAddOnlyParentEntity() {
        ParentEntity user = Instancio.create(ParentEntity.class);

        when(permissionContext.getUser()).thenReturn(user);
        when(permissionContext.getResourceType()).thenReturn(ResourceType.GRADE);
        when(parentService.fetchParentEntity(user.getId())).thenReturn(user);

        enricher.enrich(permissionContext, resourceAccessContext);

        verify(permissionContext).putAttribute(PermissionContextAttributeKey.PARENT_ENTITY, user);
        verify(permissionContext, never()).putAttribute(eq(PermissionContextAttributeKey.SCHEDULE_VERSION_ENTITY), any());
        verify(parentService).fetchParentEntity(user.getId());
        verify(scheduleVersionService, never()).fetchScheduleVersion(anyInt());
    }

    @Test
    void enrich_WhenScheduleVersionIdNotFound_ShouldHandleGracefully() {
        ParentEntity user = Instancio.create(ParentEntity.class);

        when(permissionContext.getUser()).thenReturn(user);
        when(permissionContext.getResourceType()).thenReturn(ResourceType.SCHEDULE);
        when(parentService.fetchParentEntity(user.getId())).thenReturn(user);
        when(resourceAccessContext.getAccessedMethodParameter("scheduleVersionId")).thenReturn(null);

        enricher.enrich(permissionContext, resourceAccessContext);

        verify(permissionContext).putAttribute(PermissionContextAttributeKey.PARENT_ENTITY, user);
        verify(scheduleVersionService, never()).fetchScheduleVersion(anyInt());
    }
}