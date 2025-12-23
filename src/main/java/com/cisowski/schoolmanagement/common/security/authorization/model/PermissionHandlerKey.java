package com.cisowski.schoolmanagement.common.security.authorization.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PermissionHandlerKey {
    private UserType userType;
    private ResourceType resourceType;
}
