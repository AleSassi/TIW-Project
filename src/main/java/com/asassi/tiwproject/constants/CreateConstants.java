package com.asassi.tiwproject.constants;

public enum CreateConstants {

    Username("username"),
    FileType("selectedContentType"),
    ParentFolders("parentFolders"),
    InvalidMainFolderNameError("folderNameError"),
    InvalidParentFolderNameError("parentFolderNameError");

    private final String rawValue;

    CreateConstants(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getRawValue() {
        return rawValue;
    }

}
