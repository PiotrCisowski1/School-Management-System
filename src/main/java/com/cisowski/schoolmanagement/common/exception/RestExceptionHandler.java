package com.cisowski.schoolmanagement.common.exception;

import com.cisowski.schoolmanagement.common.exception.type.EmailAlreadyExistsException;
import com.cisowski.schoolmanagement.common.exception.type.EntityAlreadyExistsException;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.hibernate.PropertyValueException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

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
    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
        ApiError apiError = new ApiError(NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    protected ResponseEntity<Object> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        ApiError apiError = new ApiError(CONFLICT);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(PropertyValueException.class)
    protected ResponseEntity<Object> handlePropertyValueException(PropertyValueException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        String exception = "Request's properties are not sufficient or malformed";
        apiError.setMessage(exception);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(SQLException.class)
    protected ResponseEntity<Object> handleSqlServerException(SQLException ex) {
        String message;
        if (ex.getMessage().toLowerCase().contains("insert statement conflicted with the foreign key constraint"))
            message = "Error occured while processing request: insert statement conflicted with DB constraint. Check data and try again.";
        else
            message = "Error occured while processing request";
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(message);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(AccountStatusException.class)
    protected ResponseEntity<Object> handleAccountStatusException(AccountStatusException accountStatusException) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN);
        String exMessage = "This account is locked";
        apiError.setMessage(exMessage);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> handleAuthorizationException(AccessDeniedException accessDeniedException) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN);
        String exMessage = "You are not authorized to access this resource";
        apiError.setMessage(exMessage);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(SignatureException.class)
    protected ResponseEntity<Object> handleTokenSignatureException(SignatureException signatureException) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN);
        String exMessage = "The token signature is invalid";
        apiError.setMessage(exMessage);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity<Object> handleExpiredTokenException(ExpiredJwtException expiredJwtException) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN);
        String exMessage = "The token has expired";
        apiError.setMessage(exMessage);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(MalformedJwtException.class)
    protected ResponseEntity<Object> handleMalformedTokenException(MalformedJwtException malformedJwtException) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN);
        String exMessage = "Given token is malformed";
        apiError.setMessage(exMessage);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exception) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(exception.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<Object> handleNullPointerException(NullPointerException exception) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
        String exMessage = "Targeted data does not exist or is empty";
        apiError.setMessage(exMessage);
        return buildResponseEntity(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage,
                        (existing, replacement) -> existing
                ));

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage("Validation failed");
        apiError.setValidatorErrors(validationErrors);

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(SpecificationBrokenException.class)
    protected ResponseEntity<Object> handleSpecificationBrokenException(SpecificationBrokenException exception){
        ApiError apiError = new ApiError(NOT_ACCEPTABLE);
        String exMessage = exception.getMessage();
        apiError.setMessage(exMessage);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    protected ResponseEntity<Object> handleEntityExistsEx(EntityAlreadyExistsException exception){
        ApiError apiError = new ApiError(CONFLICT);
        String exMessage = exception.getMessage();
        apiError.setMessage(exMessage);
        return buildResponseEntity(apiError);
    }
}
