package edu.asu.diging.gilesecosystem.web.files.impl;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesFileStorageException;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;

public class StorageStatus {

    private IFile file;
    private GilesProcessingException exception;
    private RequestStatus status;

    public StorageStatus(IFile file, GilesProcessingException exception,
            RequestStatus status) {
        super();
        this.file = file;
        this.exception = exception;
        this.status = status;
    }

    public IFile getFile() {
        return file;
    }

    public void setFile(IFile file) {
        this.file = file;
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

}
