package com.sq022groupA.escalayt.exception;

import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredJwtTokenException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtTokenException(ExpiredJwtTokenException exception, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Expired Token")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(PasswordsDoNotMatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordsDoNotMatchException(PasswordsDoNotMatchException exception, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Passwords Do Not Match")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException exception, WebRequest request) {
        ErrorResponse errorResponse= ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("Username Already Exists")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ErrorResponse> handleMessagingException(MessagingException exception, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Error sending email: " + exception.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception exception, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // theis method handles general DoesNotExistException
    // so no need to create another exception for Does not exist again
    // you can just throw this error and pass the unique custom error message
    @ExceptionHandler(DoesNotExistException.class)
    public ResponseEntity<String> handleTicketDoesNotExistException(DoesNotExistException ex){
        // Returns a ResponseEntity with a status of 404 Not Found
        // and the exception message as the response body.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // custom exception
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> customException(CustomException ex){
        // Returns a ResponseEntity with a status of 404 Not Found
        // and the exception message as the response body.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
}
