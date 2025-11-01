package com.cisowski.schoolmanagement.unit.authorization.policy;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionHandlerKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.common.security.authorization.policy.TeacherAvailabilityAccessPolicy;
import com.cisowski.schoolmanagement.common.security.authorization.handler.ResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherAvailabilityAccessPolicyTest {

    @Mock
    private Map<PermissionHandlerKey, ResourcePermissionHandler<?>> handlers;

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @Test
    void getResourceType_ShouldReturnTeacherAvailability() {
        TeacherAvailabilityAccessPolicy policy = new TeacherAvailabilityAccessPolicy(handlers);

        ResourceType result = policy.getResourceType();

        assertEquals(ResourceType.TEACHER_AVAILABILITY, result);
    }

    @Test
    void canAccess_WhenUserIsAdministrator_ShouldReturnTrue() {
        TeacherAvailabilityAccessPolicy policy = new TeacherAvailabilityAccessPolicy(handlers);
        when(permissionContext.isUser(UserType.ADMINISTRATOR)).thenReturn(true);

        boolean result = policy.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
        verify(permissionContext).isUser(UserType.ADMINISTRATOR);
        verifyNoInteractions(handlers);
    }

    @Test
    void canAccess_WhenUserIsNotAdministratorAndHandlerDoesNotExist_ShouldThrowException() {
        TeacherAvailabilityAccessPolicy policy = new TeacherAvailabilityAccessPolicy(handlers);
        UserType userType = UserType.TEACHER;
        PermissionHandlerKey key = new PermissionHandlerKey(userType, ResourceType.TEACHER_AVAILABILITY);

        when(permissionContext.isUser(UserType.ADMINISTRATOR)).thenReturn(false);
        when(permissionContext.getPrimaryUserType()).thenReturn(userType);
        when(handlers.get(key)).thenReturn(null);

        UnsupportedOperationException thrown = assertThrows(
                UnsupportedOperationException.class,
                () -> policy.canAccess(permissionContext, resourceAccessContext)
        );

        assertTrue(thrown.getMessage().contains("No permission handler found"));
        assertTrue(thrown.getMessage().contains(userType.name()));
        assertTrue(thrown.getMessage().contains(ResourceType.TEACHER_AVAILABILITY.name()));
        verify(handlers).get(key);
    }

    @Test
    void canAccess_WhenUserPrimaryTypeIsNull_ShouldThrowException() {
        TeacherAvailabilityAccessPolicy policy = new TeacherAvailabilityAccessPolicy(handlers);

        when(permissionContext.isUser(UserType.ADMINISTRATOR)).thenReturn(false);
        when(permissionContext.getPrimaryUserType()).thenReturn(null);

        UnsupportedOperationException thrown = assertThrows(
                UnsupportedOperationException.class,
                () -> policy.canAccess(permissionContext, resourceAccessContext)
        );

        assertTrue(thrown.getMessage().contains("No permission handler found"));
        assertTrue(thrown.getMessage().contains("null"));
        assertTrue(thrown.getMessage().contains(ResourceType.TEACHER_AVAILABILITY.name()));
    }
}