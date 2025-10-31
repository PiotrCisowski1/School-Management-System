package com.cisowski.schoolmanagement.common.security.authorization.handler;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;

public abstract class BaseResourcePermissionHandler<T> implements ResourcePermissionHandler<T> {

    protected boolean requiresFiltering = false;

    protected final String buildLogMessage(String action){
        return String.format("Checking %s permission for %s resource with permission to %s",
                getSupportedUserType(),
                getSupportedResourceType(),
                action);
    }

    protected boolean isAdministrator(PermissionContext context){
        return context.isUser(UserType.ADMINISTRATOR);
    }

    public boolean requiresFiltering() {
        return requiresFiltering;
    }
}
