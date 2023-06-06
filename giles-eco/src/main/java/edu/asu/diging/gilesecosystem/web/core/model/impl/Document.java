package edu.asu.diging.gilesecosystem.web.core.model.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentType;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IPage;
import edu.asu.diging.gilesecosystem.web.core.model.ITask;

@Entity
@Table(indexes={
        @Index(columnList="uploadId", name="IDX_UPLOAD_ID"),
        @Index(columnList="username", name="IDX_USERNAME"),
        @Index(columnList="requestId", name="IDX_REQUEST_ID")
})
public class Document implements IDocument {

    @Id private String id;
    private String uploadId;
    private String username;
    private String createdDate;
    private String uploadedFile;
    private String extractedText;
    @ElementCollection @LazyCollection(LazyCollectionOption.FALSE) private List<String> fileIds;
    private DocumentAccess access;
    private transient List<IFile> files;
    private DocumentType documentType;
    private int pageCount;
    
    @ElementCollection 
    @LazyCollection(LazyCollectionOption.FALSE) 
    private List<String> textFileIds;
    
    @OneToMany(cascade=CascadeType.ALL, targetEntity=Page.class, mappedBy="document") 
    @LazyCollection(LazyCollectionOption.FALSE) 
    private List<IPage> pages;

    @OneToMany(cascade=CascadeType.ALL, targetEntity=Task.class) 
    @LazyCollection(LazyCollectionOption.FALSE) 
    @JoinTable(name="Document_Task")
    private List<ITask> tasks;
    
    private String requestId;

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.core.impl.IDocument#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.core.impl.IDocument#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.core.impl.IDocument#getUploadId()
     */
    @Override
    public String getUploadId() {
        return uploadId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.core.impl.IDocument#setUploadId(java.lang.String)
     */
    @Override
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.core.impl.IDocument#getCreatedDate()
     */
    @Override
    public String getCreatedDate() {
        return createdDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.core.impl.IDocument#setCreatedDate(java.util.Date)
     */
    @Override
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.core.impl.IDocument#getFileIds()
     */
    @Override
    public List<String> getFileIds() {
        return fileIds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.core.impl.IDocument#setFileIds(java.util.List)
     */
    @Override
    public void setFileIds(List<String> fileIds) {
        this.fileIds = fileIds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.core.impl.IDocument#getAccess()
     */
    @Override
    public DocumentAccess getAccess() {
        return access;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.core.impl.IDocument#setAccess(edu.asu.giles.core.impl.
     * DocumentAccess)
     */
    @Override
    public void setAccess(DocumentAccess access) {
        this.access = access;
    }

    @Override
    public List<IFile> getFiles() {
        return files;
    }

    @Override
    public void setFiles(List<IFile> files) {
        this.files = files;
    }

    @Override
    public DocumentType getDocumentType() {
        if (documentType == null) {
            return DocumentType.SINGLE_PAGE;
        }
        return documentType;
    }

    @Override
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    @Override
    public int getPageCount() {
        return pageCount;
    }

    @Override
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    @Override
    public List<String> getTextFileIds() {
        return textFileIds;
    }

    @Override
    public void setTextFileIds(List<String> textFileIds) {
        this.textFileIds = textFileIds;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Method to retrieve list of pages of this document.
     * This method will never return null.
     * @return 
     */
    @Override
    public List<IPage> getPages() {
        if (pages == null) {
            pages = new ArrayList<IPage>();
        }
        return pages;
    }

    @Override
    public void setPages(List<IPage> pages) {
        this.pages = pages;
    }

    /**
     * File that was originally uploaded.
     */
    @Override
    public String getUploadedFileId() {
        return uploadedFile;
    }

    @Override
    public void setUploadedFileId(String uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    @Override
    public String getExtractedTextFileId() {
        return extractedText;
    }

    @Override
    public void setExtractedTextFileId(String extractedText) {
        this.extractedText = extractedText;
    }

    @Override
    public List<ITask> getTasks() {
        return tasks;
    }

    @Override
    public void setTasks(List<ITask> tasks) {
        this.tasks = tasks;
    }
    
    @Override
    public String getRequestId() {
        return requestId;
    }

    @Override
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
