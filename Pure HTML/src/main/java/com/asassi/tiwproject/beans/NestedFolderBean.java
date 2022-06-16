package com.asassi.tiwproject.beans;

import com.asassi.tiwproject.beans.FolderBean;
import com.asassi.tiwproject.constants.FolderType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NestedFolderBean {

    private String username;
    private int folderNumber;
    private String name;
    private LocalDateTime creationDate;
    private List<NestedFolderBean> subfolders;

    public NestedFolderBean(FolderBean parent, List<FolderBean> subfolders) {
        this.username = parent.getUsername();
        this.folderNumber = parent.getFolderNumber();
        this.name = parent.getName();
        this.creationDate = parent.getCreationDate();
        if (subfolders != null) {
            this.subfolders = subfolders.stream().map((subfolder) -> new NestedFolderBean(subfolder, null)).toList();
        } else {
            this.subfolders = new ArrayList<>();
        }
    }

    public String getUsername() {
        return username;
    }

    public int getFolderNumber() {
        return folderNumber;
    }

    public String getFolderNumberStr() {
        int a = getFolderNumber();
        return Integer.toString(a);
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getCreationDateString() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(getCreationDate());
    }

    public List<NestedFolderBean> getSubfolders() {
        return subfolders;
    }

    @Override
    public String toString() {
        return "NestedFolderBean{" +
                "name='" + name + '\'' +
                ", subfolders=" + subfolders +
                '}';
    }
}
