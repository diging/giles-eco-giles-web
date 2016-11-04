package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.IRequestFactory;
import edu.asu.diging.gilesecosystem.requests.IStorageRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.impl.StorageRequest;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IUpload;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesFileStorageException;
import edu.asu.diging.gilesecosystem.web.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.web.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.files.IDocumentDatabaseClient;
import edu.asu.diging.gilesecosystem.web.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.rest.processing.TemporaryFilesController;
import edu.asu.diging.gilesecosystem.web.service.IFileSystemHelper;
import edu.asu.diging.gilesecosystem.web.service.IFileTypeHandler;
import edu.asu.diging.gilesecosystem.web.service.kafka.IRequestProducer;
import edu.asu.diging.gilesecosystem.web.service.processing.IDistributedStorageManager;
import edu.asu.diging.gilesecosystem.web.service.properties.IPropertiesManager;

@Service
public class DistributedStorageManager implements IDistributedStorageManager {
    
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("tmpStorageManager") 
    private IFileStorageManager storageManager;

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
    
    @Autowired
    private IFileSystemHelper filesHelper;
    
    @Autowired
    private ApplicationContext ctx;
    
    private Map<String, FileType> fileTypes;
    
    @PostConstruct
    public void init() {
        requestFactory.config(StorageRequest.class);
        fileTypes = new HashMap<String, FileType>();
        
        // register what content types should be treated as what file type
        Map<String, IFileTypeHandler> ctxMap = ctx.getBeansOfType(IFileTypeHandler.class);
        Iterator<Entry<String, IFileTypeHandler>> iter = ctxMap.entrySet().iterator();
        
        while(iter.hasNext()){
            Entry<String, IFileTypeHandler> handlerEntry = iter.next();
            IFileTypeHandler handler = (IFileTypeHandler) handlerEntry.getValue();
            for (String type : handler.getHandledFileTypes()) {
                fileTypes.put(type, handler.getHandledFileType());
            }
        }
    }

    /* (non-Javadoc)
     * @see edu.asu.giles.service.processing.impl.IStorageManager#storeFile(java.lang.String, edu.asu.giles.core.IFile, edu.asu.giles.core.IDocument, edu.asu.giles.core.IUpload, byte[])
     */
    @Override
    public RequestStatus storeFile(String username, IFile file, IDocument document,
            IUpload upload, byte[] content) throws GilesFileStorageException, UnstorableObjectException {
        storageManager.saveFile(username, upload.getId(), document.getId(), file.getFilename(), content);
        try {
            filesDbClient.saveFile(file);
        } catch (UnstorableObjectException e) {
            logger.error("Could not store file.", e);
            return RequestStatus.FAILED;
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
        request.setFileType(fileTypes.get(file.getContentType()));
        document.setRequest(request);
        
        documentsDbClient.saveDocument(document);
        
        
        try {
            requestProducer.sendRequest(request, propertyManager.getProperty(IPropertiesManager.KAFKA_TOPIC_STORAGE_REQUEST));
        } catch (MessageCreationException e) {
            request.setStatus(RequestStatus.FAILED);
            documentsDbClient.saveDocument(document);
            throw new GilesFileStorageException(e);
        }
        
        request.setStatus(RequestStatus.SUBMITTED);
        documentsDbClient.saveDocument(document);
        return request.getStatus();
    }
    
    /* (non-Javadoc)
     * @see edu.asu.giles.service.processing.impl.IStorageManager#getFileUrl(edu.asu.giles.core.IFile)
     */
    @Override
    public String getFileUrl(IFile file) {
        String gilesUrl = propertyManager.getProperty(IPropertiesManager.GILES_URL).trim();
        String endpoint = TemporaryFilesController.GET_CONTENT_URL;
        
        return gilesUrl + endpoint.replace(TemporaryFilesController.FILE_ID_PLACEHOLDER, file.getId());
    }
    
    @Override
    public byte[] getFileContent(IFile file) {
        return filesHelper.getFileContent(file, storageManager);
    }
}
