package com.cisowski.schoolmanagement.common.security.authorization.context;

import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.users.common.model.AuthorityEntity;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
public class PermissionContext {
    private UserEntity user;
    private Set<AuthorityEntity> authorities;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Map<PermissionContextAttributeKey, Object> attributes = new HashMap<>();
    private ResourceType resourceType;
    private ResourceActionType action;

    public boolean isUser(UserType userType) {
        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equalsIgnoreCase(userType.toString()));
    }

    public <T> T getAttribute(PermissionContextAttributeKey key) {
        return (T) attributes.get(key);
    }

    public <T> void putAttribute(PermissionContextAttributeKey key, T value) {
        if (!key.getExpectedType().isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Attribute type mismatch for " + key);
        }
        attributes.put(key, value);
    }

    public UserType getPrimaryUserType(){
        if(this.isUser(UserType.TEACHER))
            return UserType.TEACHER;
        if(this.isUser(UserType.STUDENT))
            return UserType.STUDENT;
        if(this.isUser(UserType.PARENT))
            return UserType.PARENT;
        return null;
    }
}
