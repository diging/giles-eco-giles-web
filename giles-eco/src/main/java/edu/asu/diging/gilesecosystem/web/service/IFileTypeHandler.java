package edu.asu.diging.gilesecosystem.web.service;

import java.util.List;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IUpload;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesFileStorageException;

public interface IFileTypeHandler {
    
    public static final String DEFAULT_HANDLER = "DEFAULT";

    /**
     * Returns a list of content types that this handler can
     * process.
     * @return
     */
    List<String> getHandledFileTypes();
    
    FileType getHandledFileType();
    
    boolean processFile(String username, IFile file, IDocument document, IUpload upload, byte[] content) throws GilesFileStorageException;

    String getRelativePathOfFile(IFile file);
    
    String getFileUrl(IFile file);
    
    byte[] getFileContent(IFile file);
}
