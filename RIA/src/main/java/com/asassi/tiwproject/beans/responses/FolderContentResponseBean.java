package com.asassi.tiwproject.beans.responses;

import com.asassi.tiwproject.beans.DocumentBean;

import java.util.List;

public class FolderContentResponseBean {

    private String folderName;
    private DocumentBean[] documents;

    public DocumentBean[] getDocuments() {
        return documents;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setDocuments(List<DocumentBean> documents) {
        this.documents = documents.toArray(new DocumentBean[0]);
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
