package com.ing.credit_module.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler
    public ResponseEntity handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        if (exception instanceof HttpException) {
            return handleApiException((HttpException) exception);
        }

        if (exception instanceof CreditBusinessRuleException) {
            return handleBusinessRuleException(exception.getMessage());
        }

        if (exception instanceof AccessDeniedException) {
            return handleAccessDeniedException();
        }

        if (exception instanceof MethodArgumentNotValidException) {
            return handleValidationException((MethodArgumentNotValidException) exception);
        }

        return handleUnexpectedException();
    }

    private ResponseEntity handleApiException(HttpException httpException) {

        return new ResponseEntity(httpException.getMessage(), httpException.getHttpStatus());
    }

    private ResponseEntity handleValidationException(MethodArgumentNotValidException exception) {
        List<String> errors = new ArrayList<String>();

        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        for (ObjectError error : exception.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        return new ResponseEntity(errors, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity handleAccessDeniedException() {
        return new ResponseEntity("Denied", HttpStatus.FORBIDDEN);
    }

    private ResponseEntity handleUnexpectedException() {
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity handleBusinessRuleException(String errorMessage) {
        return new ResponseEntity(errorMessage, HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
