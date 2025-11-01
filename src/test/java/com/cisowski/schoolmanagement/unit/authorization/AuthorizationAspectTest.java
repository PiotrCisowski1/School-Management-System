package com.cisowski.schoolmanagement.unit.authorization;

import com.cisowski.schoolmanagement.common.exception.type.AccessDeniedException;
import com.cisowski.schoolmanagement.common.security.authorization.AuthorizationAspect;
import com.cisowski.schoolmanagement.common.security.authorization.AuthorizationService;
import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.resolver.ResourceResolver;
import com.cisowski.schoolmanagement.users.common.model.UserDetailsEntity;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import org.aspectj.lang.JoinPoint;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorizationAspectTest {

    @Mock
    private AuthorizationService authService;

    @Mock
    private ResourceResolver resolver;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthorizationAspect authorizationAspect;

    @Test
    void checkAccess_WhenUserHasPermission_ShouldNotThrowException() {
        UserEntity user = Instancio.create(UserEntity.class);
        UserDetailsEntity userDetailsEntity = new UserDetailsEntity(user);
        RequiresPermission permission = mock(RequiresPermission.class);
        ResourceAccessContext accessContext = mock(ResourceAccessContext.class);

        when(permission.resource()).thenReturn(Instancio.create(ResourceType.class));
        when(permission.action()).thenReturn(Instancio.create(ResourceActionType.class));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetailsEntity);

        SecurityContextHolder.setContext(securityContext);
        when(resolver.resolve(joinPoint, permission)).thenReturn(accessContext);
        when(authService.canAccess(user, accessContext, permission.resource(), permission.action())).thenReturn(true);

        assertDoesNotThrow(() -> authorizationAspect.checkAccess(joinPoint, permission));

        verify(authService).canAccess(user, accessContext, permission.resource(), permission.action());
    }

    @Test
    void checkAccess_WhenUserLacksPermission_ShouldThrowAccessDeniedException() {
        UserEntity user = Instancio.create(UserEntity.class);
        UserDetailsEntity userDetailsEntity = new UserDetailsEntity(user);
        RequiresPermission permission = mock(RequiresPermission.class);
        ResourceAccessContext accessContext = mock(ResourceAccessContext.class);
        ResourceType resourceType = Instancio.create(ResourceType.class);
        ResourceActionType actionType = Instancio.create(ResourceActionType.class);

        when(permission.resource()).thenReturn(resourceType);
        when(permission.action()).thenReturn(actionType);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetailsEntity);

        SecurityContextHolder.setContext(securityContext);
        when(resolver.resolve(joinPoint, permission)).thenReturn(accessContext);
        when(authService.canAccess(user, accessContext, resourceType, actionType)).thenReturn(false);

        AccessDeniedException thrown = assertThrows(
                AccessDeniedException.class,
                () -> authorizationAspect.checkAccess(joinPoint, permission)
        );

        verify(authService).canAccess(user, accessContext, resourceType, actionType);
    }

    @Test
    void checkAccess_WhenSecurityContextNull_ShouldThrowException() {
        RequiresPermission permission = mock(RequiresPermission.class);

        SecurityContextHolder.clearContext();

        assertThrows(NullPointerException.class, () -> authorizationAspect.checkAccess(joinPoint, permission));
    }

    @Test
    void checkAccess_WhenAuthenticationNull_ShouldThrowException() {
        RequiresPermission permission = mock(RequiresPermission.class);

        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(NullPointerException.class, () -> authorizationAspect.checkAccess(joinPoint, permission));
    }

    @Test
    void checkAccess_WhenPrincipalNotUserDetailsEntity_ShouldThrowException() {
        RequiresPermission permission = mock(RequiresPermission.class);
        String invalidPrincipal = "invalid_principal";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(invalidPrincipal);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(ClassCastException.class, () -> authorizationAspect.checkAccess(joinPoint, permission));
    }
}
