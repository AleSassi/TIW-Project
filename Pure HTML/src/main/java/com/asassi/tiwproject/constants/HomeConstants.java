package com.asassi.tiwproject.constants;

public enum HomeConstants {

    Username("username"),
    Hierarchy("hierarchy"),
    DocToMoveID("documentID"),
    DocToMoveName("documentName"),
    DocToMoveSrcFolderID("srcFolderID"),
    DocToMoveSrcFolderName("srcFolderName"),
    ErrorMessage("errorMessage");

    private final String rawValue;

    HomeConstants(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getRawValue() {
        return rawValue;
    }

}
