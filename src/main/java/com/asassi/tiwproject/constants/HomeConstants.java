package com.asassi.tiwproject.constants;

public enum HomeConstants {

    Username("username"),
    MainFolders("mainFolders"),
    Subfolders("subfolders");

    private final String rawValue;

    HomeConstants(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getRawValue() {
        return rawValue;
    }

}
