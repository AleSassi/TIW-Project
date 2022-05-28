package com.asassi.tiwproject.beans.responses;

import com.asassi.tiwproject.beans.DocumentBean;

public class CreateDocumentResponseBean {

    private String documentNameError;
    private String documentExtensionError;
    private String documentContentError;

    public String getDocumentNameError() {
        return documentNameError;
    }

    public void setDocumentNameError(String documentNameError) {
        this.documentNameError = documentNameError;
    }

    public String getDocumentExtensionError() {
        return documentExtensionError;
    }

    public void setDocumentExtensionError(String documentExtensionError) {
        this.documentExtensionError = documentExtensionError;
    }

    public String getDocumentContentError() {
        return documentContentError;
    }

    public void setDocumentContentError(String documentContentError) {
        this.documentContentError = documentContentError;
    }
}
