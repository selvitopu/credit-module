package com.ing.credit_module.exception;

public class UserNotFoundException extends ApiException {
    private static final long serialVersionUID = -5258959358527382145L;

    public UserNotFoundException(String message) {
        super(message);
    }
}
