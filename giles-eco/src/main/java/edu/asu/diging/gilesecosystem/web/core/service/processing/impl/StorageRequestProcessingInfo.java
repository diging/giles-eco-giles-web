package edu.asu.diging.gilesecosystem.web.core.service.processing.impl;

import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IUpload;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IProcessingInfo;

public class StorageRequestProcessingInfo implements IProcessingInfo {

    private String providerUsername;
    private String provider;
    private IFile file;
    private IDocument document;
    private IUpload upload;
    private byte[] content;
    
    public String getProviderUsername() {
        return providerUsername;
    }
    public void setProviderUsername(String providerUsername) {
        this.providerUsername = providerUsername;
    }
    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }
    public IFile getFile() {
        return file;
    }
    public void setFile(IFile file) {
        this.file = file;
    }
    public IDocument getDocument() {
        return document;
    }
    public void setDocument(IDocument document) {
        this.document = document;
    }
    public IUpload getUpload() {
        return upload;
    }
    public void setUpload(IUpload upload) {
        this.upload = upload;
    }
    public byte[] getContent() {
        return content;
    }
    public void setContent(byte[] content) {
        this.content = content;
    }
}
