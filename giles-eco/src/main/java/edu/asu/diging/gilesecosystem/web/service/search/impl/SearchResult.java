package edu.asu.diging.gilesecosystem.web.service.search.impl;

public class SearchResult {

    private String fileId;
    private String storedFileId;
    private String documentId;
    private String username;
    
    public String getFileId() {
        return fileId;
    }
    public void setFileId(String id) {
        this.fileId = id;
    }
    public String getStoredFileId() {
        return storedFileId;
    }
    public void setStoredFileId(String storedFileId) {
        this.storedFileId = storedFileId;
    }
    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
