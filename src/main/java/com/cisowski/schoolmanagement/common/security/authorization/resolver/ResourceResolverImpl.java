package com.cisowski.schoolmanagement.common.security.authorization.resolver;

import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Component
public class ResourceResolverImpl implements ResourceResolver {

    @Override
    public ResourceAccessContext resolve(JoinPoint joinPoint, RequiresPermission permission) {
        DbLogger.info("Creating ResourceAccessContext");
        Object[] args = joinPoint.getArgs();
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();

        ResourceAccessContext accessContext = new ResourceAccessContext(permission.resource(), permission.action());

        for(int i = 0; i < args.length; i++)
            accessContext.put(paramNames[i], args[i]);

        return accessContext;
    }

    @Override
    public ResourceType getResourceType() {
        return null;
    }
}
