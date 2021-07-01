package edu.asu.diging.gilesecosystem.web.core.service.processing;

import edu.asu.diging.gilesecosystem.web.core.model.IFile;

public interface IDistributedStorageManager {

    public abstract String getFileUrl(IFile file);

    public abstract byte[] getFileContent(IFile file);

}