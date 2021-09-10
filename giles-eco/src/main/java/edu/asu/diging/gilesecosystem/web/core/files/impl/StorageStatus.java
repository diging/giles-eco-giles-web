package edu.asu.diging.gilesecosystem.web.core.files.impl;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;

public class StorageStatus {

    private IDocument document;
    private IFile file;
    private GilesProcessingException exception;
    private RequestStatus status;

    public StorageStatus(IDocument document, IFile file, GilesProcessingException exception,
            RequestStatus status) {
        super();
        this.document = document;
        this.file = file;
        this.exception = exception;
        this.status = status;
    }

    public IDocument getDocument() {
        return document;
    }

    public void setDocument(IDocument doc) {
        this.document = doc;
    }

    public GilesProcessingException getException() {
        return exception;
    }

    public void setException(GilesProcessingException exception) {
        this.exception = exception;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public IFile getFile() {
        return file;
    }

    public void setFile(IFile file) {
        this.file = file;
    }

}
