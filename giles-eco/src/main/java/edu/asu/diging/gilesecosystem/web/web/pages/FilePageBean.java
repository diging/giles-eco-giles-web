package edu.asu.diging.gilesecosystem.web.web.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.ProcessingStatus;

public class FilePageBean implements IFile, BeanWithAdditionalFiles {

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
    private ProcessingStatus processingStatus;
    
    private RequestStatus textExtractionStatus;
    private RequestStatus imageExtractionStatus;
    private RequestStatus storedStatus;
    private RequestStatus ocrStatus;
    
    private Map<String, List<AdditionalFilePageBean>> additionalFiles = new HashMap<>();  
    
    private List<Badge> badges = new ArrayList<>();
    
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
    public void setUsernameForStorage(String usernameForStorage) {
    }
    public String getUsernameForStorage() {
        return null;
    }
    public String getRequestId() {
        return null;
    }
    public void setRequestId(String requestId) {
    }
    public void setDownloadUrl(String downloadUrl) {
    }
    public String getDownloadUrl() {
        return null;
    }
    public void setStorageId(String storageId) {
    }
    public String getStorageId() {
        return null;
    }
    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }
    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }
    public RequestStatus getTextExtractionStatus() {
        return textExtractionStatus;
    }

    public void setTextExtractionStatus(RequestStatus processedStatus) {
        this.textExtractionStatus = processedStatus;
    }

    public RequestStatus getStoredStatus() {
        return storedStatus;
    }

    public void setStoredStatus(RequestStatus storedStatus) {
        this.storedStatus = storedStatus;
    }
    public RequestStatus getImageExtractionStatus() {
        return imageExtractionStatus;
    }
    public void setImageExtractionStatus(RequestStatus imageExtractionStatus) {
        this.imageExtractionStatus = imageExtractionStatus;
    }
    public RequestStatus getOcrStatus() {
        return ocrStatus;
    }
    public void setOcrStatus(RequestStatus ocrStatus) {
        this.ocrStatus = ocrStatus;
    }
    public List<Badge> getBadges() {
        return badges;
    }
    public void setBadges(List<Badge> badges) {
        this.badges = badges;
    }
    @Override
    public Map<String, List<AdditionalFilePageBean>> getAdditionalFiles() {
        return additionalFiles;
    }
    public void setAdditionalFiles(Map<String, List<AdditionalFilePageBean>> additionalFiles) {
        this.additionalFiles = additionalFiles;
    }
    @Override
    public void setRecordId(String recordId) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public String getRecordId() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void setGroupId(String groupId) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public String getGroupId() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void setOldFileVersionIds(List<String> fileVersionIds) {}
    @Override
    public List<String> getOldFileVersionIds() {
        return null;
    }
}
