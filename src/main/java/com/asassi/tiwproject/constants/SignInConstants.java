package com.asassi.tiwproject.constants;

public enum SignInConstants {

    UsernameErrorInfo("usernameError"),
    PasswordErrorInfo("passwordError"),
    ValidatedUsername("validatedUsername");

    private final String rawValue;

    SignInConstants(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getRawValue() {
        return rawValue;
    }

}
