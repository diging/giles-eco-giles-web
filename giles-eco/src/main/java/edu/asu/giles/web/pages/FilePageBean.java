package edu.asu.giles.web.pages;

import org.apache.commons.lang3.NotImplementedException;

import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.IFile;

public class FilePageBean implements IFile {

    private String uploadId;
    private String filename;
    private String username;
    private String documentId;
    private String id;
    private String uploadDate;
    private DocumentAccess access;
    private String contentType;
    private long size;
    private String filepath; 
    private String metadataLink;
    private String derivedFrom;
    
    public String getUploadId() {
        return uploadId;
    }
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUploadDate() {
        return uploadDate;
    }
    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }
    public DocumentAccess getAccess() {
        return access;
    }
    public void setAccess(DocumentAccess access) {
        this.access = access;
    }
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public long getSize() {
        return size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public String getFilepath() {
        return filepath;
    }
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    public String getMetadataLink() {
        return metadataLink;
    }
    public void setMetadataLink(String metadataLink) {
        this.metadataLink = metadataLink;
    }
    public void setDerivedFrom(String derivedFrom) {
        this.derivedFrom = derivedFrom;
    }
    public String getDerivedFrom() {
        return derivedFrom;
    }
   
    public IFile clone() {
        throw new NotImplementedException("Not yet implemented.");
    }
    
}
