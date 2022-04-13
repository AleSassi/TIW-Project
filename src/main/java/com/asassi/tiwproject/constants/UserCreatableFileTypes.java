package com.asassi.tiwproject.constants;

public enum UserCreatableFileTypes {

    Folder(0),
    Subfolder(1),
    Document(2);

    private final int rawValue;

    UserCreatableFileTypes(int rawValue) {
        this.rawValue = rawValue;
    }

    public int getRawValue() {
        return rawValue;
    }

    public static int restrictToValidRawValue(int originalRawValue) {
        return Math.max(0, Math.min(2, originalRawValue));
    }

    public static UserCreatableFileTypes getFileType(int rawValue) {
        if (rawValue == 0) {
            return Folder;
        } else if (rawValue == 1) {
            return Subfolder;
        } else {
            return Document;
        }
    }
}
