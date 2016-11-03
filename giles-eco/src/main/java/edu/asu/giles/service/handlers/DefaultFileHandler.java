package edu.asu.giles.service.handlers;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.exceptions.GilesFileStorageException;
import edu.asu.giles.exceptions.UnstorableObjectException;
import edu.asu.giles.files.IFileStorageManager;
import edu.asu.giles.files.IFilesDatabaseClient;
import edu.asu.giles.service.IFileTypeHandler;
import edu.asu.giles.service.properties.IPropertiesManager;

@PropertySource("classpath:/config.properties")
@Service
public class DefaultFileHandler extends AbstractFileHandler implements IFileTypeHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    @Qualifier("fileStorageManager")
    private IFileStorageManager storageManager;
    
    @Autowired
    private IPropertiesManager propertyManager;
    
    @Autowired
    private IFilesDatabaseClient databaseClient;
    
    
    @Override
    public List<String> getHandledFileTypes() {
        List<String> types = new ArrayList<String>();
        types.add(DEFAULT_HANDLER);
        return types;
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
    public String getFileUrl(IFile file) {
        String relativePath = getRelativePathOfFile(file);
        String gilesUrl = propertyManager.getProperty(IPropertiesManager.GILES_URL);
        String gilesDigilibEndpoint = propertyManager.getProperty(IPropertiesManager.GILES_DIGILIB_ENDPOINT);
        
        try {
            return gilesUrl + gilesDigilibEndpoint + "?fn=" + URLEncoder.encode(relativePath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("Could not encode path.", e);
            return gilesUrl + gilesDigilibEndpoint + "?fn=" + relativePath;
        }
    }

    @Override
    protected IFileStorageManager getStorageManager() {
        return storageManager;
    }

}
