package com.asassi.tiwproject.constants;

public enum ContentCreationFormField {

    ContentType("contentType"),
    FolderName("folderName"),
    ParentFolderNumber("parentFolder"),
    DocumentName("documentName"),
    DocumentFileType("documentFileType"),
    DocumentContent("documentContent");

    private final String rawValue;

    ContentCreationFormField(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getRawValue() {
        return rawValue;
    }

}
