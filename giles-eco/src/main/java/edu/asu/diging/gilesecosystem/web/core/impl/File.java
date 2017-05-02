package edu.asu.diging.gilesecosystem.web.core.impl;

import javax.jdo.annotations.Index;
import javax.persistence.Entity;
import javax.persistence.Id;

import edu.asu.diging.gilesecosystem.web.core.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;

/**
 * @deprecated
 *      Use {@link edu.asu.diging.gilesecosystem.web.domain.impl.File} instead. This
 *      class is only kept for migration purposes.
 * @author jdamerow
 *
 */
@Deprecated
@Entity
public class File implements IFile {

    @Id private String id;
    @Index private String uploadId;
    private String filename;
    @Index private String username;
    @Index private String documentId;
    private String uploadDate;
    private DocumentAccess access;
    private String contentType;
    private long size;
    @Index private String filepath; 
    private String derivedFrom;
    @Index private String usernameForStorage;
    @Index private String requestId;
    private String storageId;
    private String downloadUrl;
    @Index private ProcessingStatus processingStatus;

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
}
