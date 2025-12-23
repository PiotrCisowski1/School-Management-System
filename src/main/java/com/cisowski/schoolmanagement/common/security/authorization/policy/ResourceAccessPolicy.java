package com.cisowski.schoolmanagement.common.security.authorization.policy;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import org.springframework.data.jpa.domain.Specification;

public interface ResourceAccessPolicy<T> {
    boolean canAccess(PermissionContext context, ResourceAccessContext accessContext);
    ResourceType getResourceType();
}
