package com.asassi.tiwproject.constants;

public enum PageConstants {

    Default("/"),
    SignUp("/signup"),
    Home("/home"),
    Logout("/logout");

    private final String rawValue;

    PageConstants(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getRawValue() {
        return rawValue;
    }

}
