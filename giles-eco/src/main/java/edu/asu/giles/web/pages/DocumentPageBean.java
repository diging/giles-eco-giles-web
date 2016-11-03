package edu.asu.giles.web.pages;

import java.util.List;

import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.DocumentType;
import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IPage;
import edu.asu.giles.service.requests.IRequest;

public class DocumentPageBean implements IDocument {
    private String id;
    private String documentId;
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
    
    private IRequest request;
    private String statusLabel;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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
    public void setRequest(IRequest request) {
        this.request = request;
    }
    public IRequest getRequest() {
        return request;
    }
    public String getStatusLabel() {
        return statusLabel;
    }
    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }
    
}
