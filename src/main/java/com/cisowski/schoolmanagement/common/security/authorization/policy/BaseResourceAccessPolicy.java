package com.cisowski.schoolmanagement.common.security.authorization.policy;

import com.cisowski.schoolmanagement.common.exception.type.AccessDeniedException;
import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.ResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionHandlerKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;

import java.util.Map;

public abstract class BaseResourceAccessPolicy<T> implements ResourceAccessPolicy<T> {

    private final Map<PermissionHandlerKey, ResourcePermissionHandler<?>> handlers;

    public BaseResourceAccessPolicy(Map<PermissionHandlerKey, ResourcePermissionHandler<?>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public boolean canAccess(PermissionContext context, ResourceAccessContext accessContext) {
        if (context.isUser(UserType.ADMINISTRATOR)) return true;

        UserType userType = context.getPrimaryUserType();
        PermissionHandlerKey key = new PermissionHandlerKey(userType, getResourceType());

        ResourcePermissionHandler<?> handler = handlers.get(key);
        if(handler == null) {
            Integer userId = -1;
            if(context.getUser() != null)
                userId = context.getUser().getId();
            throw new AccessDeniedException(userId.toString(), context.getResourceType().name(), context.getAction().name());
        }
        return handler.canAccess(context, accessContext);
    }
}

