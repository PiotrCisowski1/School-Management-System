package com.cisowski.schoolmanagement.unit.authorization;

import com.cisowski.schoolmanagement.common.security.authorization.AuthorizationService;
import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContextBuilder;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.policy.ResourceAccessPolicy;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private PermissionContextBuilder permissionContextBuilder;

    @Mock
    private Map<ResourceType, ResourceAccessPolicy<?>> policies;

    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    void canAccess_WhenPolicyExistsAndReturnsTrue_ShouldReturnTrue() {
        UserEntity user = Instancio.create(UserEntity.class);
        ResourceAccessContext accessContext = mock(ResourceAccessContext.class);
        ResourceType resourceType = Instancio.create(ResourceType.class);
        ResourceActionType actionType = Instancio.create(ResourceActionType.class);
        PermissionContext permissionContext = mock(PermissionContext.class);
        ResourceAccessPolicy policy = mock(ResourceAccessPolicy.class);

        when(permissionContextBuilder.build(user, resourceType, actionType)).thenReturn(permissionContext);
        when(policies.get(resourceType)).thenReturn(policy);
        when(policy.canAccess(permissionContext, accessContext)).thenReturn(true);

        boolean result = authorizationService.canAccess(user, accessContext, resourceType, actionType);

        assertTrue(result);
        verify(permissionContextBuilder).build(user, resourceType, actionType);
        verify(policies).get(resourceType);
        verify(policy).canAccess(permissionContext, accessContext);
    }

    @Test
    void canAccess_WhenPolicyExistsAndReturnsFalse_ShouldReturnFalse() {
        UserEntity user = Instancio.create(UserEntity.class);
        ResourceAccessContext accessContext = mock(ResourceAccessContext.class);
        ResourceType resourceType = Instancio.create(ResourceType.class);
        ResourceActionType actionType = Instancio.create(ResourceActionType.class);
        PermissionContext permissionContext = mock(PermissionContext.class);
        ResourceAccessPolicy policy = mock(ResourceAccessPolicy.class);

        when(permissionContextBuilder.build(user, resourceType, actionType)).thenReturn(permissionContext);
        when(policies.get(resourceType)).thenReturn(policy);
        when(policy.canAccess(permissionContext, accessContext)).thenReturn(false);

        boolean result = authorizationService.canAccess(user, accessContext, resourceType, actionType);

        assertFalse(result);
        verify(permissionContextBuilder).build(user, resourceType, actionType);
        verify(policies).get(resourceType);
        verify(policy).canAccess(permissionContext, accessContext);
    }

    @Test
    void canAccess_WhenPolicyNotFound_ShouldThrowUnsupportedOperationException() {
        UserEntity user = Instancio.create(UserEntity.class);
        ResourceAccessContext accessContext = mock(ResourceAccessContext.class);
        ResourceType resourceType = Instancio.create(ResourceType.class);
        ResourceActionType actionType = Instancio.create(ResourceActionType.class);
        PermissionContext permissionContext = mock(PermissionContext.class);

        when(permissionContextBuilder.build(user, resourceType, actionType)).thenReturn(permissionContext);
        when(policies.get(resourceType)).thenReturn(null);

        UnsupportedOperationException thrown = assertThrows(
                UnsupportedOperationException.class,
                () -> authorizationService.canAccess(user, accessContext, resourceType, actionType)
        );

        assertTrue(thrown.getMessage().contains("No ResourceAccessPolicy found for resource type: " + resourceType));
        verify(permissionContextBuilder).build(user, resourceType, actionType);
        verify(policies).get(resourceType);
    }
}