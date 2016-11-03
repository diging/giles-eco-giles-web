package edu.asu.giles.files.impl;

import edu.asu.giles.core.IFile;
import edu.asu.giles.exceptions.GilesFileStorageException;

public class StorageStatus {

    public final static int SUCCESS = 0;
    public final static int FAILURE = 1;

    private IFile file;
    private GilesFileStorageException exception;
    private int status;

    public StorageStatus(IFile file, GilesFileStorageException exception,
            int status) {
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

    /**
     * Returns if an upload was successful.
     * 
     * @return 0 = SUCCESS, 1 = FAILURE
     */
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
