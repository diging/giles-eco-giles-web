package edu.asu.diging.gilesecosystem.web.service;

import java.util.List;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.web.core.IFile;

public interface IFileTypeHandler {
    
    public static final String DEFAULT_HANDLER = "DEFAULT";

    /**
     * Returns a list of content types that this handler can
     * process.
     * @return
     */
    List<String> getHandledFileTypes();
    
    FileType getHandledFileType();
    
    String getFileUrl(IFile file);
    
    byte[] getFileContent(IFile file);
}
