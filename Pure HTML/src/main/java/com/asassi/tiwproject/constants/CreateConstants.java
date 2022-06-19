package com.asassi.tiwproject.constants;

public enum CreateConstants {

    Username("username"),
    FileType("selectedContentType"),
    Hierarchy("hierarchy"),
    InvalidObjectTypeError("objectTypeError"),
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
