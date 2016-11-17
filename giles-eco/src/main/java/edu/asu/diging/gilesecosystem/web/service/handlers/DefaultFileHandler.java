package edu.asu.diging.gilesecosystem.web.service.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IUpload;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesFileStorageException;
import edu.asu.diging.gilesecosystem.web.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.IFileTypeHandler;

@PropertySource("classpath:/config.properties")
@Service
public class DefaultFileHandler extends AbstractFileHandler implements IFileTypeHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    @Qualifier("fileStorageManager")
    private IFileStorageManager storageManager;
    
    @Autowired
    private IFilesDatabaseClient databaseClient;
    
    
    @Override
    public List<String> getHandledFileTypes() {
        List<String> types = new ArrayList<String>();
        types.add(DEFAULT_HANDLER);
        return types;
    }
    
    @Override
    public FileType getHandledFileType() {
        return FileType.OTHER;
    }

    @Override
    public boolean processFile(String username, IFile file, IDocument document, IUpload upload, byte[] content) throws GilesFileStorageException {
        storageManager.saveFile(username, upload.getId(), document.getDocumentId(), file.getFilename(), content);
        try {
            databaseClient.saveFile(file);
        } catch (UnstorableObjectException e) {
            logger.error("Could not store file.", e);
            return false;
        }
        return true;
    }

    @Override
    public String getRelativePathOfFile(IFile file) {
        String directory = storageManager.getFileFolderPath(file.getUsername(), file.getUploadId(), file.getDocumentId());
        return directory + File.separator + file.getFilename();
    }

    @Override
    protected IFileStorageManager getStorageManager() {
        return storageManager;
    }

}
