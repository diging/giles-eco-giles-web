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
import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.IRequestFactory;
import edu.asu.diging.gilesecosystem.requests.IStorageRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.requests.impl.StorageRequest;
import edu.asu.diging.gilesecosystem.requests.kafka.IRequestProducer;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IUpload;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesFileStorageException;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.files.IDocumentDatabaseClient;
import edu.asu.diging.gilesecosystem.web.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.rest.processing.TemporaryFilesController;
import edu.asu.diging.gilesecosystem.web.service.IFileSystemHelper;
import edu.asu.diging.gilesecosystem.web.service.IFileTypeHandler;
import edu.asu.diging.gilesecosystem.web.service.processing.IDistributedStorageManager;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingInfo;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingPhase;
import edu.asu.diging.gilesecosystem.web.service.processing.ProcessingPhaseName;
import edu.asu.diging.gilesecosystem.web.service.properties.IPropertiesManager;

@Service
public class DistributedStorageManager extends ProcessingPhase<StorageRequestProcessingInfo> implements IDistributedStorageManager {
    
    final Logger logger = LoggerFactory.getLogger(getClass());
    
    public final static String REQUEST_PREFIX = "STREQ";

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

    @Override
    public ProcessingPhaseName getPhaseName() {
        return ProcessingPhaseName.STORAGE;
    }

    @Override
    protected IRequest createRequest(IFile file, IProcessingInfo info)
            throws GilesProcessingException {
        StorageRequestProcessingInfo storageInfo = (StorageRequestProcessingInfo) info;
        
        String username = storageInfo.getProvider() + "_" + storageInfo.getProviderUsername();
        file.setUsernameForStorage(username);
        // generate request id for file
        file.setRequestId(filesDbClient.generateId(REQUEST_PREFIX, filesDbClient::getFileByRequestId));
        
        IUpload upload = storageInfo.getUpload();
        IDocument document = storageInfo.getDocument();
        byte[] content = storageInfo.getContent();
        try {
            storageManager.saveFile(username, upload.getId(), document.getId(), file.getFilename(), content);
        } catch (GilesFileStorageException e1) {
            throw new GilesProcessingException(e1);
        }
        try {
            filesDbClient.saveFile(file);
        } catch (UnstorableObjectException e) {
            throw new GilesProcessingException(e);
        }
        
        IStorageRequest request = null;
        try {
            request = requestFactory.createRequest(upload.getId());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new GilesProcessingException(e);
        }
        
        request.setRequestId(file.getRequestId());
        request.setDocumentId(document.getId());
        request.setPathToFile(storageManager.getFileFolderPath(username, upload.getId(), document.getId()));
        request.setDownloadUrl(getFileUrl(file));
        request.setFileType(fileTypes.get(file.getContentType()));
        request.setUploadDate(file.getUploadDate());
        request.setFilename(file.getFilename());
        request.setUsername(username);
        return request;
    }

    @Override
    protected String getTopic() {
        return propertyManager.getProperty(IPropertiesManager.KAFKA_TOPIC_STORAGE_REQUEST);
    }
    
}
