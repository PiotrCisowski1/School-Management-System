package com.cisowski.schoolmanagement.common.exception.type;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String userId, String resource, String action) {
        super(String.format("User with ID %s lacks %s permission for resource %s",
                userId,
                action,
                resource));
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
