package com.cisowski.schoolmanagement.common.exception.type;

public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(Class entityType, String identificator){
        super(buildExceptionMessage(entityType, identificator));
    }

    private static String buildExceptionMessage(Class entityType, String identificator){
        return String.format("Entity (%s) already exists with identificator: %s", entityType.getSimpleName(), identificator);
    }
}
