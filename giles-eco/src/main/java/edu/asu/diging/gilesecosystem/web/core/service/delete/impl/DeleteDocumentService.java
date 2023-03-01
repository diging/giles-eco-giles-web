package edu.asu.diging.gilesecosystem.web.core.service.delete.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.IRequestFactory;
import edu.asu.diging.gilesecosystem.requests.IStorageDeletionRequest;
import edu.asu.diging.gilesecosystem.requests.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.requests.impl.StorageDeletionRequest;
import edu.asu.diging.gilesecosystem.requests.kafka.IRequestProducer;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.service.IFileTypeHandler;
import edu.asu.diging.gilesecosystem.web.core.service.delete.IDeleteDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;

@Service
public class DeleteDocumentService implements IDeleteDocumentService {
    
    public final static String REQUEST_PREFIX = "";
    
    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private IRequestFactory<IStorageDeletionRequest, StorageDeletionRequest> requestFactory;
    
    @Autowired
    private ApplicationContext ctx;
    
    @Autowired
    private ISystemMessageHandler messageHandler;
    
    @Autowired
    private IRequestProducer requestProducer;
    
    @Autowired
    private IPropertiesManager propertyManager;
    
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
    }
    
    @Override
    @Async
    public void deleteDocument(IDocument document) {
        List<IFile> files = filesManager.getFilesOfDocument(document);
        for(IFile file : files) {
            deleteFile(file);
            
        }
    }
    
    private void sendDeleteRequest(IFile file) {
        try {
            IRequest storageDeletionRequest = createRequest(file);
            requestProducer.sendRequest(storageDeletionRequest, getTopic());
        } catch (GilesProcessingException | MessageCreationException e) {
            messageHandler.handleMessage("Could not create Request", e, MessageType.ERROR);
        }
        
    }
    
    private void deleteFile(IFile file) {
        sendDeleteRequest(file);
        
        filesManager.deleteFile(file.getId());
    }
    
    private IRequest createRequest(IFile file) throws GilesProcessingException {
        IStorageDeletionRequest storageDeletionRequest = null;
        try {
            storageDeletionRequest = requestFactory.createRequest(file.getRequestId(), file.getUploadId());
            storageDeletionRequest.setDocumentId(file.getDocumentId());
            storageDeletionRequest.setFileId(file.getId());
            storageDeletionRequest.setUploadId(file.getUploadId());
            storageDeletionRequest.setUsername(file.getUsernameForStorage());
            storageDeletionRequest.setFileType(fileTypes.get(file.getContentType()));
            
        } catch (InstantiationException | IllegalAccessException e) {
            throw new GilesProcessingException(e);
        }
        return storageDeletionRequest;
    }
    
    private String getTopic() {
        return propertyManager.getProperty(Properties.KAFKA_TOPIC_STORAGE_DELETION_REQUEST);
    }
}
