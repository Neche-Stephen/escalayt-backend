package com.sq022groupA.escalayt.exception;

// Custom exception class for handling cases where a user is not found.
public class UserNotFoundException extends RuntimeException{
    // Constructor that accepts a message describing the exception.
    public UserNotFoundException(String message){
        super(message);
    }
}
