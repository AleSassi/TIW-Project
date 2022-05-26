package com.asassi.tiwproject.beans;

import com.asassi.tiwproject.constants.FolderType;

import java.time.LocalDateTime;
import java.util.List;

public class NestedFolderBean extends JSONSendable {

    private String username;
    private int folderNumber;
    private String name;
    private LocalDateTime creationDate;
    private FolderType folderType;
    private NestedFolderBean[] subfolders;

    public NestedFolderBean(FolderBean parent, List<FolderBean> subfolders) {
        this.username = parent.getUsername();
        this.folderNumber = parent.getFolderNumber();
        this.name = parent.getName();
        this.creationDate = parent.getCreationDate();
        if (parent.getFolderType().getRawValue() == FolderType.Main.getRawValue()) {
            this.folderType = FolderType.Main;
        } else {
            this.folderType = FolderType.Subfolder;
        }
        if (subfolders != null) {
            this.subfolders = subfolders.stream().map((subfolder) -> new NestedFolderBean(subfolder, null)).toList().toArray(new NestedFolderBean[0]);
        }
    }

}
