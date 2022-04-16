package com.asassi.tiwproject.constants;

public enum SubfolderDetailConstants {

    Username("username"),
    FolderName("folderName"),
    Documents("documents");

    private final String rawValue;

    SubfolderDetailConstants(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getRawValue() {
        return rawValue;
    }

}
