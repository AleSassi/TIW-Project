package com.asassi.tiwproject.constants;

public enum HomeConstants {

    Username("username"),
    FolderHierarchy("folders");

    private final String rawValue;

    HomeConstants(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getRawValue() {
        return rawValue;
    }

}
