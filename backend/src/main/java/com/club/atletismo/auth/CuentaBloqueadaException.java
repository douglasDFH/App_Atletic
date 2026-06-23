package com.club.atletismo.auth;

public class CuentaBloqueadaException extends RuntimeException {
    public CuentaBloqueadaException(String message) {
        super(message);
    }
}
