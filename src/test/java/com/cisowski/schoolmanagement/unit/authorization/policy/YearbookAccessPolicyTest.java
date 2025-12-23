package com.cisowski.schoolmanagement.unit.authorization.policy;

import com.cisowski.schoolmanagement.common.exception.type.AccessDeniedException;
import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionHandlerKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.security.authorization.policy.YearbookAccessPolicy;
import com.cisowski.schoolmanagement.common.security.authorization.handler.ResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YearbookAccessPolicyTest {

    @Mock
    private Map<PermissionHandlerKey, ResourcePermissionHandler<?>> handlers;

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @Test
    void getResourceType_ShouldReturnYearbook() {
        YearbookAccessPolicy policy = new YearbookAccessPolicy(handlers);

        ResourceType result = policy.getResourceType();

        assertEquals(ResourceType.YEARBOOK, result);
    }

    @Test
    void canAccess_WhenUserIsAdministrator_ShouldReturnTrue() {
        YearbookAccessPolicy policy = new YearbookAccessPolicy(handlers);
        when(permissionContext.isUser(UserType.ADMINISTRATOR)).thenReturn(true);

        boolean result = policy.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
        verify(permissionContext).isUser(UserType.ADMINISTRATOR);
        verifyNoInteractions(handlers);
    }

    @Test
    void canAccess_WhenUserIsNotAdministratorAndHandlerDoesNotExist_ShouldThrowException() {
        YearbookAccessPolicy policy = new YearbookAccessPolicy(handlers);
        UserType userType = UserType.TEACHER;
        PermissionHandlerKey key = new PermissionHandlerKey(userType, ResourceType.YEARBOOK);
        ResourceType resourceType = Instancio.create(ResourceType.class);
        ResourceActionType resourceActionType = Instancio.create(ResourceActionType.class);

        when(permissionContext.getResourceType()).thenReturn(resourceType);
        when(permissionContext.getAction()).thenReturn(resourceActionType);
        when(permissionContext.isUser(UserType.ADMINISTRATOR)).thenReturn(false);
        when(permissionContext.getPrimaryUserType()).thenReturn(userType);
        when(handlers.get(key)).thenReturn(null);

        AccessDeniedException thrown = assertThrows(
                AccessDeniedException.class,
                () -> policy.canAccess(permissionContext, resourceAccessContext)
        );

        assertTrue(thrown.getMessage().contains(resourceType.name()));
        assertTrue(thrown.getMessage().contains(resourceActionType.name()));
        verify(handlers).get(key);
    }

    @Test
    void canAccess_WhenUserPrimaryTypeIsNull_ShouldThrowException() {
        YearbookAccessPolicy policy = new YearbookAccessPolicy(handlers);
        ResourceType resourceType = Instancio.create(ResourceType.class);
        ResourceActionType resourceActionType = Instancio.create(ResourceActionType.class);

        when(permissionContext.getResourceType()).thenReturn(resourceType);
        when(permissionContext.getAction()).thenReturn(resourceActionType);
        when(permissionContext.isUser(UserType.ADMINISTRATOR)).thenReturn(false);
        when(permissionContext.getPrimaryUserType()).thenReturn(null);

        AccessDeniedException thrown = assertThrows(
                AccessDeniedException.class,
                () -> policy.canAccess(permissionContext, resourceAccessContext)
        );

        assertTrue(thrown.getMessage().contains(resourceType.name()));
        assertTrue(thrown.getMessage().contains(resourceActionType.name()));
    }
}