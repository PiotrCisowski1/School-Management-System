package com.cisowski.schoolmanagement.unit.authorization.handler.parent;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.parent.ParentYearbookPermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
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
class ParentYearbookPermissionHandlerTest {

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private ParentYearbookPermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnParent() {
        UserType result = handler.getSupportedUserType();

        assertEquals(UserType.PARENT, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnYearbook() {
        ResourceType result = handler.getSupportedResourceType();

        assertEquals(ResourceType.YEARBOOK, result);
    }

    @Test
    void canAccess_WhenActionIsReadAndYearbookIdMatchesChildsYearbook_ShouldReturnTrue() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        Integer yearbookId = yearbook.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(yearbookId);

        parent.setChildren(List.of(child));
        child.setYearbook(yearbook);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndYearbookIdDoesNotMatchAnyChildsYearbook_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        YearbookEntity differentYearbook = Instancio.create(YearbookEntity.class);
        Integer yearbookId = differentYearbook.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(yearbookId);

        parent.setChildren(List.of(child));
        child.setYearbook(yearbook);

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
    void canAccess_WhenActionIsReadAndYearbookIdIsNull_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(null);

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
    void canAccess_WhenMultipleChildrenAndOneHasMatchingYearbook_ShouldReturnTrue() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child1 = Instancio.create(StudentEntity.class);
        StudentEntity child2 = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook1 = Instancio.create(YearbookEntity.class);
        YearbookEntity yearbook2 = Instancio.create(YearbookEntity.class);
        Integer yearbookId = yearbook2.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(yearbookId);

        parent.setChildren(List.of(child1, child2));
        child1.setYearbook(yearbook1);
        child2.setYearbook(yearbook2);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenMultipleChildrenAndNoneHaveMatchingYearbook_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child1 = Instancio.create(StudentEntity.class);
        StudentEntity child2 = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook1 = Instancio.create(YearbookEntity.class);
        YearbookEntity yearbook2 = Instancio.create(YearbookEntity.class);
        YearbookEntity differentYearbook = Instancio.create(YearbookEntity.class);
        Integer yearbookId = differentYearbook.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(yearbookId);

        parent.setChildren(List.of(child1, child2));
        child1.setYearbook(yearbook1);
        child2.setYearbook(yearbook2);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenParentHasNoChildren_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        Integer yearbookId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("yearbookId")).thenReturn(yearbookId);

        parent.setChildren(Collections.emptyList());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void requiresFiltering_ShouldReturnFalse() {
        boolean result = handler.requiresFiltering();

        assertFalse(result);
    }
}