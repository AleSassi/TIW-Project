package com.asassi.tiwproject.constants;

public enum ContentCreationFormField {

    ContentType("contentType"),
    FolderName("folderName"),
    ParentFolderName("parentFolder"),
    SubfolderNumber("subfolderNumber"),
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
