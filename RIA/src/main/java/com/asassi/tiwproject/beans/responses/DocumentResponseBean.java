package com.asassi.tiwproject.beans.responses;

import com.asassi.tiwproject.beans.DocumentBean;

import java.util.List;

public class DocumentResponseBean {

    private String folderName;
    private DocumentBean document;

    public DocumentBean getDocument() {
        return document;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setDocument(DocumentBean document) {
        this.document = document;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

}
