package edu.asu.diging.gilesecosystem.web.core.service.delete.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.IRequestFactory;
import edu.asu.diging.gilesecosystem.requests.IStorageDeletionRequest;
import edu.asu.diging.gilesecosystem.requests.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.requests.impl.StorageDeletionRequest;
import edu.asu.diging.gilesecosystem.requests.kafka.IRequestProducer;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalProcessingRequestService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalUploadService;
import edu.asu.diging.gilesecosystem.web.core.service.delete.IDeleteDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;

/**
 * This class handles the deletion of a document.
*/

@Service
public class DeleteDocumentService implements IDeleteDocumentService {
    
    public final static String REQUEST_PREFIX = "DELREQ";
    
    @Autowired
    private IRequestFactory<IStorageDeletionRequest, StorageDeletionRequest> requestFactory;
    
    @Autowired
    private IRequestProducer requestProducer;
    
    @Autowired
    private IPropertiesManager propertyManager;
    
    @Autowired
    private ITransactionalFileService fileService;
    
    @Autowired
    private ITransactionalUploadService uploadService;
    
    @Autowired
    private ITransactionalDocumentService documentService;
    
    @Autowired
    private ITransactionalProcessingRequestService processingRequestService;
    
    @PostConstruct
    public void init() {
        requestFactory.config(StorageDeletionRequest.class);
    }
    
    @Override
    @Async
    public void initiateDeletion(IDocument document) throws GilesProcessingException, MessageCreationException, UnstorableObjectException {
        IRequest storageDeletionRequest = createRequest(document);
        requestProducer.sendRequest(storageDeletionRequest, propertyManager.getProperty(Properties.KAFKA_TOPIC_STORAGE_DELETION_REQUEST));
    }
    
    @Override
    public void completeDeletion(IDocument document) {
        fileService.deleteFiles(document.getId());
        processingRequestService.deleteRequestsByDocumentId(document.getId());
        documentService.deleteDocument(document.getId());
        deleteUpload(document.getUploadId());
    }
    
    private void deleteUpload(String uploadId) {
        // if an upload has multiple documents and only one of the documents is deleted the upload does not have to be deleted.
        if(documentService.getDocumentsByUploadId(uploadId).isEmpty()) {
            uploadService.deleteUpload(uploadId);
        }
    }
    
    private IRequest createRequest(IDocument document) throws GilesProcessingException, UnstorableObjectException {
        IStorageDeletionRequest storageDeletionRequest = null;
        try {
            String requestId = documentService.generateRequestId(REQUEST_PREFIX);
            storeRequestId(requestId, document);
            storageDeletionRequest = requestFactory.createRequest(requestId, document.getUploadId());
            storageDeletionRequest.setDocumentId(document.getId());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new GilesProcessingException(e);
        } 
        return storageDeletionRequest;
    }
    
    private void storeRequestId(String requestId, IDocument document) throws UnstorableObjectException {
        document.setRequestId(requestId);
        documentService.saveDocument(document);
    }
}
