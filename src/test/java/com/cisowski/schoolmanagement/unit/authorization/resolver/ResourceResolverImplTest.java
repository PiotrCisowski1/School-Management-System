package com.cisowski.schoolmanagement.unit.authorization.resolver;

import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.resolver.ResourceResolverImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceResolverImplTest {

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private RequiresPermission permission;

    private final ResourceResolverImpl resourceResolver = new ResourceResolverImpl();

    @Test
    void resolve_ShouldCreateResourceAccessContextWithParameters() {
        ResourceType resourceType = Instancio.create(ResourceType.class);
        ResourceActionType actionType = Instancio.create(ResourceActionType.class);
        String[] paramNames = {"id", "name", "value"};
        Object[] args = {123, "testName", Instancio.create(Object.class)};

        when(permission.resource()).thenReturn(resourceType);
        when(permission.action()).thenReturn(actionType);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(args);
        when(methodSignature.getParameterNames()).thenReturn(paramNames);

        ResourceAccessContext result = resourceResolver.resolve(joinPoint, permission);

        assertNotNull(result);
        assertEquals(resourceType, result.getResourceType());
        assertEquals(actionType, result.getActionType());

        for(int i = 0; i < args.length; i++) {
            assertEquals(args[i], result.getAccessedMethodParameter(paramNames[i]));
        }

        verify(joinPoint).getArgs();
        verify(methodSignature).getParameterNames();
        verify(permission).resource();
        verify(permission).action();
    }

    @Test
    void resolve_WhenNoArguments_ShouldCreateEmptyResourceAccessContext() {
        ResourceType resourceType = Instancio.create(ResourceType.class);
        ResourceActionType actionType = Instancio.create(ResourceActionType.class);
        String[] paramNames = {};
        Object[] args = {};

        when(permission.resource()).thenReturn(resourceType);
        when(permission.action()).thenReturn(actionType);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(args);
        when(methodSignature.getParameterNames()).thenReturn(paramNames);

        ResourceAccessContext result = resourceResolver.resolve(joinPoint, permission);

        assertNotNull(result);
        assertEquals(resourceType, result.getResourceType());
        assertEquals(actionType, result.getActionType());

        verify(joinPoint).getArgs();
        verify(methodSignature).getParameterNames();
    }

    @Test
    void resolve_WhenSingleArgument_ShouldCreateResourceAccessContextWithOneParameter() {
        ResourceType resourceType = Instancio.create(ResourceType.class);
        ResourceActionType actionType = Instancio.create(ResourceActionType.class);
        String[] paramNames = {"userId"};
        Object[] args = {456};

        when(permission.resource()).thenReturn(resourceType);
        when(permission.action()).thenReturn(actionType);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(args);
        when(methodSignature.getParameterNames()).thenReturn(paramNames);

        ResourceAccessContext result = resourceResolver.resolve(joinPoint, permission);

        assertNotNull(result);
        assertEquals(resourceType, result.getResourceType());
        assertEquals(actionType, result.getActionType());
        assertEquals(456, (Integer) result.getAccessedMethodParameter("userId"));

        verify(joinPoint).getArgs();
        verify(methodSignature).getParameterNames();
    }

    @Test
    void getResourceType_ShouldReturnNull() {
        ResourceType result = resourceResolver.getResourceType();

        assertNull(result);
    }
}