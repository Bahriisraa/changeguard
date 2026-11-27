package com.changeguard.change.error;

public class ChangeRejectedException extends RuntimeException {
    public ChangeRejectedException(String message) {
        super(message);
    }
}