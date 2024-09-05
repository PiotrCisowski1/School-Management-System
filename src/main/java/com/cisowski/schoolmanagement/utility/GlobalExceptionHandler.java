package com.cisowski.schoolmanagement.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final HttpStatusCode notFoundStatus = HttpStatusCode.valueOf(404);
    private final String userNotFoundMessage = "User is not found";

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User not found")
    @ResponseBody
    public ErrorResponse handleUserNotFoundException(UserNotFoundException ex){
        return new ErrorResponse() {
            @Override
            public HttpStatusCode getStatusCode() {
                return notFoundStatus;
            }

            @Override
            public ProblemDetail getBody() {
                return ProblemDetail.forStatusAndDetail(notFoundStatus, userNotFoundMessage);
            }
        };
    }
}
