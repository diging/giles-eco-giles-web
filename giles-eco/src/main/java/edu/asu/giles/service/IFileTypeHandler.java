package edu.asu.giles.service;

import java.util.List;

import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.exceptions.GilesFileStorageException;

public interface IFileTypeHandler {
    
    public static final String DEFAULT_HANDLER = "DEFAULT";

    /**
     * Returns a list of content types that this handler can
     * process.
     * @return
     */
    List<String> getHandledFileTypes();
    
    boolean processFile(String username, IFile file, IDocument document, IUpload upload, byte[] content) throws GilesFileStorageException;

    String getRelativePathOfFile(IFile file);
    
    String getFileUrl(IFile file);
    
    byte[] getFileContent(IFile file);
}
