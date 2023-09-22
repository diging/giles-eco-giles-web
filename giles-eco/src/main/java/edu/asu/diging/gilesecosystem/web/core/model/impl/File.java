package edu.asu.diging.gilesecosystem.web.core.model.impl;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.ProcessingStatus;

@Entity
@Table(indexes={
        @Index(columnList="uploadId", name="IDX_UPLOAD_ID"),
        @Index(columnList="username", name="IDX_USERNAME"),
        @Index(columnList="documentId", name="IDX_USERNAME"),
        @Index(columnList="filepath", name="IDX_FILEPATH"),
        @Index(columnList="usernameForStorage", name="IDX_USERNAME_STORAGE"),
        @Index(columnList="requestId", name="IDX_REQUEST_ID"),
        @Index(columnList="derivedFrom", name="IDX_DERIVED_FROM"),
        @Index(columnList="groupId", name="IDX_GROUP_ID"),
        @Index(columnList="recordId", name="IDX_RECORD_ID")
})
public class File implements IFile {

    @Id private String id;
    private String uploadId;
    @Lob private String filename;
    private String username;
    private String documentId;
    private String uploadDate;
    private DocumentAccess access;
    private String contentType;
    private long size;
    @Lob private String filepath; 
    private String derivedFrom;
    private String usernameForStorage;
    private String requestId;
    private String storageId;
    private String downloadUrl;
    private ProcessingStatus processingStatus;
    private String groupId;
    private String recordId;

    /**
     * List of old file version IDs for the file if the file was reprocessed.
     * When a file is reprocessed, this list is populated with the older storage IDs associated with the file in Nepomuk.
     * While the file is updated to store the newest storage ID after reprocessing.
     * It allows us to maintain a record of its previous versions. 
     * Which can be used in the deletion of stale records from storage in the future.
    */
    @ElementCollection 
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> oldFileVersionIds;

    public File() {}

    public File(String filename) {
        this.filename = filename;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.core.impl.IFile#getUploadId()
     */
    @Override
    public String getUploadId() {
        return uploadId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.core.impl.IFile#setUploadId(java.lang.String)
     */
    @Override
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.core.impl.IFile#getFilename()
     */
    @Override
    public String getFilename() {
        return filename;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.core.impl.IFile#setFilename(java.lang.String)
     */
    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.core.impl.IFile#getUsername()
     */
    @Override
    public String getUsername() {
        return username;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.core.impl.IFile#setUsername(java.lang.String)
     */
    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.core.impl.IFile#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.core.impl.IFile#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getDocumentId() {
        if (documentId == null) {
            return id;
        }
        return documentId;
    }

    @Override
    public void setDocumentId(String zoteroDocumentId) {
        this.documentId = zoteroDocumentId;
    }

    @Override
    public String getUploadDate() {
        return uploadDate;
    }

    @Override
    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    @Override
    public DocumentAccess getAccess() {
        return access;
    }

    @Override
    public void setAccess(DocumentAccess access) {
        this.access = access;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String getFilepath() {
        return filepath;
    }

    @Override
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    
    @Override
    public String getDerivedFrom() {
        return derivedFrom;
    }

    @Override
    public void setDerivedFrom(String derivedFrom) {
        this.derivedFrom = derivedFrom;
    }
    
    @Override
    public String getUsernameForStorage() {
        return usernameForStorage;
    }

    @Override
    public void setUsernameForStorage(String usernameForStorage) {
        this.usernameForStorage = usernameForStorage;
    }
    
    @Override
    public String getRequestId() {
        return requestId;
    }

    @Override
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }  

    @Override
    public String getStorageId() {
        return storageId;
    }

    @Override
    public void setStorageId(String storageId) {
        this.storageId = storageId;
    }

    @Override
    public String getDownloadUrl() {
        return downloadUrl;
    }

    @Override
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    
    @Override
    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    @Override
    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    } 
    
    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String getRecordId() {
        return recordId;
    }

    @Override
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    @Override
    public IFile clone() {
        IFile file = new File();
        file.setAccess(access);
        file.setContentType(contentType);
        file.setDocumentId(documentId);
        file.setFilename(filename);
        file.setFilepath(filepath);
        file.setSize(size);
        file.setUploadDate(uploadDate);
        file.setUploadId(uploadId);
        file.setUsername(username);
        file.setUsernameForStorage(usernameForStorage);
        return file;
    }
    
    @Override
    public void setOldFileVersionIds(List<String> fileVersionIds) {
        this.oldFileVersionIds = fileVersionIds;
    }

    @Override
    public List<String> getOldFileVersionIds() {
        return this.oldFileVersionIds;
    }
}
