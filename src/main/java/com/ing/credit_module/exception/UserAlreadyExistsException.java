package com.ing.credit_module.exception;


public class UserAlreadyExistsException extends ApiException {
    private static final long serialVersionUID = -2737319632275059973L;

    public UserAlreadyExistsException(String email) {
        super("User with email: " + email + " already registered");
    }
}
