package edu.asu.diging.gilesecosystem.web.service.processing;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IUpload;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesFileStorageException;
import edu.asu.diging.gilesecosystem.web.exceptions.UnstorableObjectException;

public interface IDistributedStorageManager {

    public abstract RequestStatus storeFile(String providerUsername, String provider, IFile file, IDocument document,
            IUpload upload, byte[] content) throws GilesFileStorageException, UnstorableObjectException;

    public abstract String getFileUrl(IFile file);

    public abstract byte[] getFileContent(IFile file);

}