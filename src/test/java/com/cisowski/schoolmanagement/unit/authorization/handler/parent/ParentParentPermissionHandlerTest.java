package com.cisowski.schoolmanagement.unit.authorization.handler.parent;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.parent.ParentParentPermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParentParentPermissionHandlerTest {

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private ParentParentPermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnParent() {
        UserType result = handler.getSupportedUserType();

        assertEquals(UserType.PARENT, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnParent() {
        ResourceType result = handler.getSupportedResourceType();

        assertEquals(ResourceType.PARENT, result);
    }

    @Test
    void canAccess_WhenActionIsReadAndParentHasAccess_ShouldReturnTrue() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        Integer parentId = parent.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("parentId")).thenReturn(parentId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndParentIdDoesNotMatch_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        Integer differentParentId = parent.getId() + 1;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("parentId")).thenReturn(differentParentId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndParentEntityIsNull_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndParentIdIsNull_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("parentId")).thenReturn(null);

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

    @Test
    void requiresFiltering_ShouldReturnFalse() {
        boolean result = handler.requiresFiltering();

        assertFalse(result);
    }
}