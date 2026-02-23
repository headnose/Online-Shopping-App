package com.headnose.personalproject.exception;

public class NotEnoughInventoryException extends RuntimeException {

    public NotEnoughInventoryException(String message) {
        super(message);
    }
}
