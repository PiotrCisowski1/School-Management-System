package com.cisowski.schoolmanagement.exception.type;

public class SpecificationBrokenException extends RuntimeException {

    public SpecificationBrokenException(String message){ super(buildMessage(message)); }

    private static String buildMessage(String message){
        return String.format("System requirement not satisfied: %s", message);
    }
}
