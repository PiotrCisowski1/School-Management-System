package com.cisowski.schoolmanagement.common.security.authorization.resolver;

import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import org.aspectj.lang.JoinPoint;

public interface ResourceResolver {
    ResourceAccessContext resolve(JoinPoint joinPoint, RequiresPermission requiresPermission);
    ResourceType getResourceType();
}
