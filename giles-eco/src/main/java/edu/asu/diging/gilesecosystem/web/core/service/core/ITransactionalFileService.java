package edu.asu.diging.gilesecosystem.web.core.service.core;

import java.util.List;
import java.util.Map;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IPage;

public interface ITransactionalFileService {

    String generateRequestId();

    void saveFile(IFile file) throws UnstorableObjectException;

    String generateFileId();

    IFile getFileById(String id);

    IFile getFileByRequestId(String requestId);

    IFile getFileByPath(String path);

    String generateRequestId(String prefix);

    List<IFile> getFilesByDerivedFrom(String derivedFromId);

    Map<String, IFile> getFilesForPage(IPage page);

    Map<String, IFile> getFilesForIds(List<String> ids);
    
    /**
     * Delete a file given the file ID.
     * @param fileId 
     *         ID of the file to be deleted
     */

    public abstract void deleteFile(String fileId);

    public abstract IFile getFileByoldFileVesrionId(String oldFileVersionId);

    public abstract List<IFile> getFilesByDocumentId(String documentId);

}