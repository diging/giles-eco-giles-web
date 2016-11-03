package edu.asu.giles.service.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.exceptions.GilesFileStorageException;
import edu.asu.giles.exceptions.MessageCreationException;
import edu.asu.giles.exceptions.UnstorableObjectException;
import edu.asu.giles.files.IDocumentDatabaseClient;
import edu.asu.giles.files.IFileStorageManager;
import edu.asu.giles.files.IFilesDatabaseClient;
import edu.asu.giles.service.IFileTypeHandler;
import edu.asu.giles.service.kafka.IRequestProducer;
import edu.asu.giles.service.properties.IPropertiesManager;
import edu.asu.giles.service.requests.IRequestFactory;
import edu.asu.giles.service.requests.IStorageRequest;
import edu.asu.giles.service.requests.impl.StorageRequest;

@PropertySource("classpath:/config.properties")
@Service
public class PdfFileHandler extends AbstractFileHandler implements IFileTypeHandler {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("tmpStorageManager") IFileStorageManager storageManager;

    @Autowired
    private IFilesDatabaseClient filesDbClient;
    
    @Autowired 
    private IDocumentDatabaseClient documentsDbClient;
    
    @Autowired
    private IRequestFactory<IStorageRequest, StorageRequest> requestFactory;
    
    @Autowired
    private IRequestProducer requestProducer;
    
    @Autowired
    private IPropertiesManager propertyManager;
    
    @PostConstruct
    public void init() {
        requestFactory.config(StorageRequest.class);
    }

    
    @Override
    public List<String> getHandledFileTypes() {
        List<String> types = new ArrayList<String>();
        types.add(MediaType.APPLICATION_PDF_VALUE);
        return types;
    }

    @Override
    public boolean processFile(String username, IFile file, IDocument document,
            IUpload upload, byte[] content) throws GilesFileStorageException {
        
        storageManager.saveFile(username, upload.getId(), document.getId(), file.getFilename(), content);
        try {
            filesDbClient.saveFile(file);
        } catch (UnstorableObjectException e) {
            logger.error("Could not store file.", e);
            return false;
        }
        
        IStorageRequest request = null;
        try {
            request = requestFactory.createRequest(upload.getId());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new GilesFileStorageException(e);
        }
        
        request.setDocumentId(document.getId());
        request.setPathToFile(storageManager.getFileFolderPath(username, upload.getId(), document.getId()));
        request.setDownloadUrl(getFileUrl(file));
        document.setRequest(request);
        try {
            documentsDbClient.saveDocument(document);
        } catch (UnstorableObjectException e1) {
            logger.error("Could not store document.", e1);
            return false;
        }
        
        try {
            requestProducer.sendRequest(request, propertyManager.getProperty(IPropertiesManager.KAFKA_TOPIC_STORAGE_REQUEST));
        } catch (MessageCreationException e) {
            throw new GilesFileStorageException(e);
        }
        
        return true;
    }

    @Override
    public String getRelativePathOfFile(IFile file) {
        String directory = storageManager.getFileFolderPath(
                file.getUsername(), file.getUploadId(), file.getDocumentId());
        return directory + File.separator + file.getFilename();
    }

    @Override
    public String getFileUrl(IFile file) {
        String gilesUrl = propertyManager.getProperty(IPropertiesManager.GILES_URL).trim();
        String pdfEndpoint = propertyManager.getProperty(IPropertiesManager.GILES_FILE_ENDPOINT).trim();
        String contentSuffix = propertyManager.getProperty(IPropertiesManager.GILES_FILE_CONTENT_SUFFIX).trim();
        
        return gilesUrl + pdfEndpoint + file.getId() + contentSuffix;
    }

    @Override
    protected IFileStorageManager getStorageManager() {
        return storageManager;
    }

}
