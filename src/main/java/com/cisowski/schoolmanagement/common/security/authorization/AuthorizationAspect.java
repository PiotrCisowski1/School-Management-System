package com.cisowski.schoolmanagement.common.security.authorization;

import com.cisowski.schoolmanagement.common.exception.type.AccessDeniedException;
import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.resolver.ResourceResolver;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.users.common.model.UserDetailsEntity;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAspect {

    private final AuthorizationService authService;
    private final ResourceResolver resolver;

    @Before("@annotation(permission)")
    public void checkAccess(JoinPoint joinPoint, RequiresPermission permission){
        UserDetailsEntity userDetailsEntity = (UserDetailsEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DbLogger.info("Starting Aspect permission check for User with ID: " + userDetailsEntity.getId());

        ResourceAccessContext accessContext = resolver.resolve(joinPoint, permission);

        if(!authService.canAccess(userDetailsEntity.getUser(), accessContext, permission.resource(), permission.action())){
            DbLogger.error(String.format(
                    "User with ID: %s, has no permission to access resource: %s, for request type: %s",
                    userDetailsEntity.getId(),
                    permission.resource(),
                    permission.action()
            ));
            throw new AccessDeniedException(userDetailsEntity.getId().toString(), permission.resource().name(), permission.action().name());
        }
    }
}