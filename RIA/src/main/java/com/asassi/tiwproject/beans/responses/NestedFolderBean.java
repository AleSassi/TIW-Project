package com.asassi.tiwproject.beans.responses;

import com.asassi.tiwproject.beans.FolderBean;
import com.asassi.tiwproject.constants.FolderType;

import java.time.LocalDateTime;
import java.util.List;

public class NestedFolderBean {

    private String username;
    private int folderNumber;
    private String name;
    private LocalDateTime creationDate;
    private NestedFolderBean[] subfolders;

    public NestedFolderBean(FolderBean parent, List<FolderBean> subfolders) {
        this.username = parent.getUsername();
        this.folderNumber = parent.getFolderNumber();
        this.name = parent.getName();
        this.creationDate = parent.getCreationDate();
        if (subfolders != null) {
            this.subfolders = subfolders.stream().map((subfolder) -> new NestedFolderBean(subfolder, null)).toList().toArray(new NestedFolderBean[0]);
        } else {
            this.subfolders = new NestedFolderBean[0];
        }
    }

}
