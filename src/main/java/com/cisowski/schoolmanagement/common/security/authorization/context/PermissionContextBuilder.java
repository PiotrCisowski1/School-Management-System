package com.cisowski.schoolmanagement.common.security.authorization.context;

import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class PermissionContextBuilder {

    public PermissionContext build(UserEntity user, ResourceType resourceType, ResourceActionType actionType){
        DbLogger.info("Creating PermissionContext");
        PermissionContext context = new PermissionContext();
        context.setUser(user);
        context.setAuthorities(new HashSet<>(user.getAuthority()));
        context.setResourceType(resourceType);
        context.setAction(actionType);

        return context;
    }
}
