package com.changeguard.change.error;

public class ChangeFreezeException extends RuntimeException {
    public ChangeFreezeException(String message) {
        super(message);
    }
}