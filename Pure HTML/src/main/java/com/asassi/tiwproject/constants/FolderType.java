package com.asassi.tiwproject.constants;

public enum FolderType {

    Main(0),
    Subfolder(1);

    private final int rawValue;

    FolderType(int rawValue) {
        this.rawValue = rawValue;
    }

    public int getRawValue() {
        return rawValue;
    }
}
