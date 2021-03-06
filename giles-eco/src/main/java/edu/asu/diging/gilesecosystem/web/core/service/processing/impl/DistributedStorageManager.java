package edu.asu.diging.gilesecosystem.web.core.service.processing.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.IStorageRequest;
import edu.asu.diging.gilesecosystem.requests.impl.StorageRequest;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.api.processing.TemporaryFilesController;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesFileStorageException;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IUpload;
import edu.asu.diging.gilesecosystem.web.core.model.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.service.IFileContentHelper;
import edu.asu.diging.gilesecosystem.web.core.service.IFileTypeHandler;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IDistributedStorageManager;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IProcessingInfo;
import edu.asu.diging.gilesecosystem.web.core.service.processing.ProcessingPhaseName;
import edu.asu.diging.gilesecosystem.web.core.service.processing.helpers.RequestHelper;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;

@Service
public class DistributedStorageManager extends ProcessingPhase<StorageRequestProcessingInfo> implements IDistributedStorageManager, Observer {
    
    final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ITransactionalFileService fileService;

    @Autowired
    @Qualifier("tmpStorageManager") 
    private IFileStorageManager storageManager;
 
    @Autowired
    private IPropertiesManager propertyManager;
    
    @Autowired
    private IFileContentHelper filesHelper;
    
    @Autowired
    private RequestHelper requestHelper;
    
    @Autowired
    private ApplicationContext ctx;
    
    private Map<String, FileType> fileTypes;
    
    @PostConstruct
    public void init() {
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
        
        propertyManager.addObserver(this);
        storageManager.setBaseDirectory(propertyManager.getProperty(Properties.GILES_TMP_FOLDER));
    }
    
    /* (non-Javadoc)
     * @see edu.asu.giles.service.processing.impl.IStorageManager#getFileUrl(edu.asu.giles.core.IFile)
     */
    @Override
    public String getFileUrl(IFile file) {
        String gilesUrl = propertyManager.getProperty(Properties.GILES_URL).trim();
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
        
        String username = requestHelper.getUsernameForStorage(storageInfo.getProvider(), storageInfo.getProviderUsername());
        file.setUsernameForStorage(username);
        // generate request id for file
        file.setRequestId(fileService.generateRequestId());
        
        IUpload upload = storageInfo.getUpload();
        IDocument document = storageInfo.getDocument();
        byte[] content = storageInfo.getContent();
        try {
            storageManager.saveFile(username, upload.getId(), document.getId(), file.getFilename(), content);
        } catch (GilesFileStorageException e1) {
            throw new GilesProcessingException(e1);
        }
        try {
            fileService.saveFile(file);
        } catch (UnstorableObjectException e) {
            throw new GilesProcessingException(e);
        }
        
        IStorageRequest request = requestHelper.createStorageRequest(file,
                storageManager.getFileFolderPath(username, upload.getId(), document.getId()), 
                getFileUrl(file), fileTypes.get(file.getContentType()), file.getRequestId());
        
        return request;
    }

    @Override
    protected String getTopic() {
        return propertyManager.getProperty(Properties.KAFKA_TOPIC_STORAGE_REQUEST);
    }

    @Override
    protected ProcessingStatus getCompletedStatus() {
        return ProcessingStatus.STORED;
    }

    @Override
    protected void postProcessing(IFile file) {
        // nothing to do here
    }

    @Override
    public void update(Observable o, Object arg) {
        String newDir = null;
        if (arg instanceof String) {
            if (arg.equals(Properties.GILES_TMP_FOLDER)) {
                newDir = propertyManager.getProperty(Properties.GILES_TMP_FOLDER);
            }
        } else if (arg instanceof Map) {
            if (((Map)arg).keySet().contains(Properties.GILES_TMP_FOLDER)) {
                newDir = ((Map)arg).get(Properties.GILES_TMP_FOLDER).toString();
            }
        }
        if (newDir != null) {
            storageManager.setBaseDirectory(newDir);
        }
    }

    @Override
    public Class<? extends IRequest> getSupportedRequestType() {
        return StorageRequest.class;
    }
    
}
