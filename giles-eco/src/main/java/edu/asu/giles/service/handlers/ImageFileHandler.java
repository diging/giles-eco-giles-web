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
import org.springframework.http.MediaType;
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
import edu.asu.giles.service.requests.FileType;

@Service
public class ImageFileHandler extends AbstractFileHandler implements IFileTypeHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IPropertiesManager propertyManager;
    
    @Autowired
    @Qualifier("fileStorageManager")
    private IFileStorageManager storageManager;
    
    @Autowired
    @Qualifier("textStorageManager")
    private IFileStorageManager textStorageManager;

    @Autowired
    private IFilesDatabaseClient filesDbClient;


    @Override
    public List<String> getHandledFileTypes() {
        List<String> fileTypes = new ArrayList<String>();
        fileTypes.add(MediaType.IMAGE_GIF_VALUE);
        fileTypes.add(MediaType.IMAGE_JPEG_VALUE);
        fileTypes.add(MediaType.IMAGE_PNG_VALUE);
        fileTypes.add(com.google.common.net.MediaType.TIFF.toString());
        return fileTypes;
    }
    
    @Override
    public FileType getHandledFileType() {
        return FileType.IMAGE;
    }

    @Override
    public boolean processFile(String username, IFile file, IDocument document,
            IUpload upload, byte[] content) throws GilesFileStorageException {
        String doOcrOnImages = propertyManager.getProperty(IPropertiesManager.OCR_IMAGES_FROM_PDFS).trim();
        
        boolean success = true;
        
        storageManager.saveFile(file.getUsername(), file.getUploadId(),
                document.getDocumentId(), file.getFilename(), content);
        
//        if (doOcrOnImages.equalsIgnoreCase(TRUE)) {
//            // if there is embedded text, let's use that one before OCRing
//            IFile textFile = doOcr(null, file, username, document);
//            document.setExtractedTextFileId(textFile.getId());
//        }
        
        try {
            filesDbClient.saveFile(file);
        } catch (UnstorableObjectException e) {
            logger.error("Could not store file.", e);
            success = false;
        }
        return success;
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
