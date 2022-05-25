package com.asassi.tiwproject.constants;

public enum CreateConstants {

    Username("username"),
    FileType("selectedContentType"),
    MainFolders("mainFolders"),
    Subfolders("subfolders"),
    InvalidMainFolderNameError("folderNameError"),
    InvalidParentFolderNameError("parentFolderNameError"),
    InvalidDocNameError("docNameError"),
    InvalidDocTypeError("docTypeError"),
    InvalidDocContentError("docContentError");

    private final String rawValue;

    CreateConstants(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getRawValue() {
        return rawValue;
    }

}
