package org.example.messagingapp.controller;

import org.example.messagingapp.exception.DuplicateEmailException;
import org.example.messagingapp.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.StringJoiner;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<ValidationErrorResponse> handleUserNotFoundError(UserNotFoundException ex) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                "Not found",
                ex.getMessage(),
                HttpStatus.NOT_FOUND
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ValidationErrorResponse> handleIllegalArgumentError(IllegalArgumentException ex) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                "Bad request",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateEmailException.class)
    protected ResponseEntity<ValidationErrorResponse> handleDuplicateEmailError(DuplicateEmailException ex) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                "Duplicate email",
                ex.getMessage(),
                HttpStatus.CONFLICT
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ValidationErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        StringJoiner joiner = new StringJoiner(", ");

        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    String errorMessage = error.getDefaultMessage();
                    joiner.add(errorMessage);
                });

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                "Validation failed",
                joiner.toString(),
                HttpStatus.BAD_REQUEST
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private record ValidationErrorResponse(String title, String error, HttpStatus status) {
    }
}
