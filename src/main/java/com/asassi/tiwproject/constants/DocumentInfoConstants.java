package com.asassi.tiwproject.constants;

public enum DocumentInfoConstants {

    Username("username"),
    DocumentName("documentName"),
    DocumentExtension("documentExtension"),
    DocumentContents("documentContents"),
    DocumentCreationDate("documentCreationDate"),
    DocumentOwner("ownerUsername"),
    ParentFolder("parentFolderName"),
    ParentFolderNumber("parentFolderNumber");

    private final String rawValue;

    DocumentInfoConstants(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getRawValue() {
        return rawValue;
    }

}
