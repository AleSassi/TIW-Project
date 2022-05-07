package com.asassi.tiwproject.constants;

public enum SignupConstants {

    UsernameErrorInfo("usernameError"),
    EmailErrorInfo("emailError"),
    PasswordErrorInfo("passwordError"),
    RepeatPasswordErrorInfo("passwordRepeatError"),
    ValidatedUsername("validatedUsername"),
    ValidatedEmail("validatedEmail");

    private final String rawValue;

    SignupConstants(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getRawValue() {
        return rawValue;
    }
}
