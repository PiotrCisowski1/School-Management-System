package com.cisowski.schoolmanagement.common.security.authorization.enricher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;

public interface PermissionContextEnricher {
    boolean supports(UserEntity user);
    void enrich(PermissionContext context, ResourceAccessContext accessContext);
}
