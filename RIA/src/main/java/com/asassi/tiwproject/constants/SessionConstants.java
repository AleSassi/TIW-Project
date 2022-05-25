package com.asassi.tiwproject.constants;

public enum SessionConstants {

    Username("username");

    private final String rawValue;

    SessionConstants(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getRawValue() {
        return rawValue;
    }
}
