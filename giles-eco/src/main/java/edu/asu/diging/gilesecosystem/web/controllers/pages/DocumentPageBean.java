package edu.asu.diging.gilesecosystem.web.controllers.pages;

import java.util.ArrayList;
import java.util.List;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.web.core.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.DocumentType;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IPage;
import edu.asu.diging.gilesecosystem.web.core.ITask;

public class DocumentPageBean implements IDocument {
    private String id;
    private String uploadId;
    private String username;
    private String createdDate;
    private String uploadedFile;
    private String extractedText;
    private List<String> fileIds;
    private DocumentAccess access;
    private transient List<IFile> files;
    private DocumentType documentType;
    private int pageCount;
    private IFile firstImage;
    private List<String> textFileIds;
    private List<IPage> pages;
    
    private List<IFile> textFiles;
    private String metadataUrl;
    private IFile uploadedFileFile;
    private IFile extractedTextFile;
    
    private String statusLabel;
    private String processingLabel;
    
    private List<Badge> badges = new ArrayList<Badge>();
    private List<Badge> externalBadges = new ArrayList<>();
    
    private List<ITask> tasks;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUploadId() {
        return uploadId;
    }
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }
    public String getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
    public List<String> getFileIds() {
        return fileIds;
    }
    public void setFileIds(List<String> fileIds) {
        this.fileIds = fileIds;
    }
    public DocumentAccess getAccess() {
        return access;
    }
    public void setAccess(DocumentAccess access) {
        this.access = access;
    }
    public List<IFile> getFiles() {
        return files;
    }
    public void setFiles(List<IFile> files) {
        this.files = files;
    }
    public DocumentType getDocumentType() {
        return documentType;
    }
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
    public int getPageCount() {
        return pageCount;
    }
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
    public IFile getFirstImage() {
        for (IFile file : files) {
            if (file.getContentType().startsWith("image/")) {
                return file;
            }
        }
        
        if (pages != null && !pages.isEmpty()) {
            if (pages.get(0) instanceof PagePageBean) {
                return ((PagePageBean)pages.get(0)).getImageFile();
            }
        }
        
        if (uploadedFileFile != null && uploadedFileFile.getContentType().startsWith("image/")) {
            return uploadedFileFile;
        }
        return null;
    }
    public void setFirstImage(IFile firstImage) {
        this.firstImage = firstImage;
    }
    public List<String> getTextFileIds() {
        return textFileIds;
    }
    public void setTextFileIds(List<String> textFileIds) {
        this.textFileIds = textFileIds;
    }
    public List<IFile> getTextFiles() {
        return textFiles;
    }
    public void setTextFiles(List<IFile> textFiles) {
        this.textFiles = textFiles;
    }
    public String getMetadataUrl() {
        return metadataUrl;
    }
    public void setMetadataUrl(String metadataUrl) {
        this.metadataUrl = metadataUrl;
    }
    public IFile getUploadedFile() {
        return uploadedFileFile;
    }
    public void setUploadedFile(IFile uploadedFile) {
        this.uploadedFileFile = uploadedFile;
    }
    public void setUploadedFile(String uploadedFile) {
        this.uploadedFile = uploadedFile;
    }
    public String getExtractedTextFileId() {
        return extractedText;
    }
    public void setExtractedTextFileId(String extractedText) {
        this.extractedText = extractedText;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    @Override
    public void setPages(List<IPage> pages) {
        this.pages = pages;
    }
    @Override
    public List<IPage> getPages() {
        return pages;
    }
    @Override
    public void setUploadedFileId(String uploadedFile) {
        this.uploadedFile = uploadedFile;
    }
    @Override
    public String getUploadedFileId() {
        return uploadedFile;
    }
    public IFile getExtractedTextFile() {
        return extractedTextFile;
    }
    public void setExtractedTextFile(IFile extractedTextFile) {
        this.extractedTextFile = extractedTextFile;
    }
    public String getStatusLabel() {
        return statusLabel;
    }
    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }
    public String getProcessingLabel() {
        return processingLabel;
    }
    public void setProcessingLabel(String processingLabel) {
        this.processingLabel = processingLabel;
    }
    public List<Badge> getBadges() {
        return badges;
    }
    public void setBadges(List<Badge> badges) {
        this.badges = badges;
    }
    public List<Badge> getExternalBadges() {
        return externalBadges;
    }
    public void setExternalBadges(List<Badge> externalBadges) {
        this.externalBadges = externalBadges;
    }
    public void setTasks(List<ITask> tasks) {
        this.tasks = tasks;
    }
    public List<ITask> getTasks() {
        return tasks;
    }
}
