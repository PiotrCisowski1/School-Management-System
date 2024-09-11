package com.cisowski.schoolmanagement.exception;

import com.cisowski.schoolmanagement.exception.type.EmailAlreadyExistsException;
import com.cisowski.schoolmanagement.exception.type.EntityNotFoundException;
import org.hibernate.PropertyValueException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {


    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String error = "Malformed JSON request";
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String error = "Media type not supported";
        return buildResponseEntity(new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, error, ex));
    }
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex){
        ApiError apiError = new ApiError(NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }
    @ExceptionHandler(EmailAlreadyExistsException.class)
    protected ResponseEntity<Object> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex){
        ApiError apiError = new ApiError(CONFLICT);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }
    @ExceptionHandler(PropertyValueException.class)
    protected ResponseEntity<Object> handlePropertyValueException(PropertyValueException ex){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        String exception = "Request's properties are not sufficient or malformed";
        apiError.setMessage(exception);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(SQLException.class)
    protected ResponseEntity<Object> handleSqlServerException(SQLException ex){
        String message;
        if(ex.getMessage().toLowerCase().contains("insert statement conflicted with the foreign key constraint"))
            message = "Error occured while processing request: insert statement conflicted with DB constraint. Check data and try again.";
        else
            message = "Error occured while processing request";
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(message);
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError){
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
