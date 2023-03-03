package edu.asu.diging.gilesecosystem.web.core.files;

import java.util.List;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.IDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;

public interface IFilesDatabaseClient extends IDatabaseClient<IFile> {

    public abstract IFile saveFile(IFile file) throws UnstorableObjectException;

    public abstract IFile getFile(String filename);

    public abstract List<IFile> getFilesByUploadId(String uploadId);

    public abstract IFile getFileById(String id);

    public abstract List<IFile> getFilesByUsername(String username);

    public abstract IFile getFileByRequestId(String requestId);

    public abstract List<IFile> getFilesByPath(String path);

    List<IFile> getFilesByDerivedFrom(String derivedFromId);

    List<IFile> getFilesForIds(List<String> ids);
    
    /**
     * Delete a file given the file ID.
     * @param fileId 
     *         ID of the file to be deleted
     */
    public abstract void deleteFile(String fileId);

    public abstract IFile getFileByoldFileVesrionId(String oldFileVersionId);
}
