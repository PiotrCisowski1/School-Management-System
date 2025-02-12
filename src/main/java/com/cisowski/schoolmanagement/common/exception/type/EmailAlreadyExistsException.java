package com.cisowski.schoolmanagement.common.exception.type;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super(buildMessage(email));
    }

    private static String buildMessage(String email) {
        return "User with given email already exists, email: " + email;
    }

}
