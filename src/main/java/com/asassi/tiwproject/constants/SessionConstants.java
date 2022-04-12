package com.asassi.tiwproject.constants;

public enum SessionConstants {

    UserHash("username");

    private final String rawValue;

    SessionConstants(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getRawValue() {
        return rawValue;
    }
}
