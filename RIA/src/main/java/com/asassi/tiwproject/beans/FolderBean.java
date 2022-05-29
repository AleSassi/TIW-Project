package com.asassi.tiwproject.beans;

import com.asassi.tiwproject.constants.FolderType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FolderBean {

    private String username;
    private int folderNumber;
    private String name;
    private LocalDateTime creationDate;
    private Integer parentFolder_folderNumber;

    public FolderBean(String username, int folderNumber, String name, LocalDateTime creationDate, Integer parentFolder_folderNumber) {
        this.username = username;
        this.folderNumber = folderNumber;
        this.name = name;
        this.creationDate = creationDate;
        this.parentFolder_folderNumber = parentFolder_folderNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getFolderNumber() {
        return folderNumber;
    }

    public String getFolderNumberStr() {
        int a = getFolderNumber();
        return Integer.toString(a);
    }

    public void setFolderNumber(int folderNumber) {
        this.folderNumber = folderNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getCreationDateString() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(getCreationDate());
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getParentFolder_folderNumber() {
        return parentFolder_folderNumber;
    }

    public void setParentFolder_folderNumber(Integer parentFolder_folderNumber) {
        this.parentFolder_folderNumber = parentFolder_folderNumber;
    }
}
