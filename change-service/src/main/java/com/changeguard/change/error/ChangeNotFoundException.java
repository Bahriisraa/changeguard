package com.changeguard.change.error;

public class ChangeNotFoundException extends RuntimeException {
    public ChangeNotFoundException(Long id) {
        super("Changement " + id + " introuvable");
    }
}