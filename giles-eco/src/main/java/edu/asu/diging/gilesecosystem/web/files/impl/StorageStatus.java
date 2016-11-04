package edu.asu.diging.gilesecosystem.web.files.impl;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesFileStorageException;

public class StorageStatus {

    private IFile file;
    private GilesFileStorageException exception;
    private RequestStatus status;

    public StorageStatus(IFile file, GilesFileStorageException exception,
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

    public GilesFileStorageException getException() {
        return exception;
    }

    public void setException(GilesFileStorageException exception) {
        this.exception = exception;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

}
