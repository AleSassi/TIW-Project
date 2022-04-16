package com.asassi.tiwproject.beans;

import java.sql.*;

public class DocumentBean {

    private String ownerUsername;
    private int parentFolderNumber;
    private int documentNumber;
    private String name;
    private String fileType;
    private Date creationDate;
    private String contents;

    public DocumentBean(String ownerUsername, int parentFolderNumber, int documentNumber, String name, String fileType, Date creationDate, String contents) {
        this.ownerUsername = ownerUsername;
        this.parentFolderNumber = parentFolderNumber;
        this.documentNumber = documentNumber;
        this.name = name;
        this.fileType = fileType;
        this.creationDate = creationDate;
        this.contents = contents;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public int getParentFolderNumber() {
        return parentFolderNumber;
    }

    public void setParentFolderNumber(int parentFolderNumber) {
        this.parentFolderNumber = parentFolderNumber;
    }

    public int getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(int documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
