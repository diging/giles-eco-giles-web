package edu.asu.giles.service.processing;

import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.exceptions.GilesFileStorageException;
import edu.asu.giles.exceptions.UnstorableObjectException;
import edu.asu.giles.service.requests.RequestStatus;

public interface IDistributedStorageManager {

    public abstract RequestStatus storeFile(String username, IFile file, IDocument document,
            IUpload upload, byte[] content) throws GilesFileStorageException, UnstorableObjectException;

    public abstract String getFileUrl(IFile file);

    public abstract byte[] getFileContent(IFile file);

}