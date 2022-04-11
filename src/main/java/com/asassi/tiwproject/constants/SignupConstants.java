package com.asassi.tiwproject.constants;

public enum SignupConstants {

    UsernameErrorInfo("usernameError"),
    PasswordErrorInfo("passwordError"),
    RepeatPasswordErrorInfo("passwordRepeatError"),
    ValidatedUsername("validatedUsername");

    private final String rawValue;

    SignupConstants(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getRawValue() {
        return rawValue;
    }
}
