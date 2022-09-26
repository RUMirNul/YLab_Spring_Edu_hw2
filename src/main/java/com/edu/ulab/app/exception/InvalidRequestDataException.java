package com.edu.ulab.app.exception;

public class InvalidRequestDataException extends RuntimeException{
    public InvalidRequestDataException(String message) {
        super(message);
    }
}
