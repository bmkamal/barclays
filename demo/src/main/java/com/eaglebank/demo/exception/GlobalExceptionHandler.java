package com.eaglebank.demo.exception;

import com.eaglebank.demo.controller.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(NotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenAccess(ForbiddenException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(UnauthorizedException ex) {
        return new ResponseEntity<>(new ErrorResponse("Invalid username or password"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ex.printStackTrace(); // Important for debugging
        return new ResponseEntity<>(new ErrorResponse("An unexpected error occurred. Please try again later."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnprocessableEntity(InvalidOperationException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
