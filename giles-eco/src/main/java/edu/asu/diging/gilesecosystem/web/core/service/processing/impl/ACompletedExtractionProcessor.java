package edu.asu.diging.gilesecosystem.web.core.service.processing.impl;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.IStorageRequest;
import edu.asu.diging.gilesecosystem.requests.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.requests.kafka.IRequestProducer;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.model.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.model.impl.File;
import edu.asu.diging.gilesecosystem.web.core.model.impl.ProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.service.IProcessingRequestService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalProcessingRequestService;
import edu.asu.diging.gilesecosystem.web.core.service.processing.helpers.RequestHelper;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.core.users.IUserManager;

public abstract class ACompletedExtractionProcessor extends ACompletedRequestProcessor {

    public final static String REQUEST_PREFIX = "STDERREQ";

    @Autowired
    protected IPropertiesManager propertyManager;
   
    @Autowired
    protected IRequestProducer requestProducer; 
    
    @Autowired
    protected RequestHelper requestHelper;
    
    @Autowired
    private ITransactionalFileService fileService;
    
    @Autowired
    private ITransactionalProcessingRequestService processingRequestService;
     
    @Autowired
    private IUserManager userManager;

    @Autowired
    private ISystemMessageHandler messageHandler;
    
    @Autowired
    private IProcessingRequestService requestService;

    protected void sendStorageRequest(IFile file, String downloadPath, String downloadUrl, FileType type) {
        IStorageRequest storageRequest;
        try {
            storageRequest = requestHelper.createStorageRequest(file, downloadPath, downloadUrl, type, fileService.generateRequestId(REQUEST_PREFIX));
        } catch (GilesProcessingException e) {
            // should not happen
            // FIXME: send to monitor app
            messageHandler.handleMessage("Could not create request.", e, MessageType.ERROR);
            return;
        }
        
        IProcessingRequest procReq = new ProcessingRequest();
        procReq.setDocumentId(file.getDocumentId());
        procReq.setFileId(file.getId());
        procReq.setSentRequest(storageRequest);
        procReq.setRequestId(storageRequest.getRequestId());
        processingRequestService.saveNewProcessingRequest(procReq);
        
        requestService.addSentRequest(storageRequest);
        try {
            requestProducer.sendRequest(storageRequest, propertyManager.getProperty(Properties.KAFKA_TOPIC_STORAGE_REQUEST));
        } catch (MessageCreationException e) {
            // FIXME: send to monitor app
            messageHandler.handleMessage("Could not send message.", e, MessageType.ERROR);
        }
    }
    
    /**

    Sends a storage request for the specified file.
    @param file The file to be stored.
    @param downloadPath The path where the file should be downloaded.
    @param downloadUrl The URL for downloading the file.
    @param type The type of the file.
    @param imageExtracted {@code true} if the image has been extracted, {@code false} otherwise.
    */
    protected void sendStorageRequest(IFile file, String downloadPath, String downloadUrl, FileType type, boolean imageExtracted) {
        IStorageRequest storageRequest;
        try {
            storageRequest = requestHelper.createStorageRequest(file, downloadPath, downloadUrl, type, fileService.generateRequestId(REQUEST_PREFIX), imageExtracted);
        } catch (GilesProcessingException e) {
            // should not happen
            // FIXME: send to monitor app
            messageHandler.handleMessage("Could not create request.", e, MessageType.ERROR);
            return;
        }
        
        IProcessingRequest procReq = new ProcessingRequest();
        procReq.setDocumentId(file.getDocumentId());
        procReq.setFileId(file.getId());
        procReq.setSentRequest(storageRequest);
        procReq.setRequestId(storageRequest.getRequestId());
        processingRequestService.saveNewProcessingRequest(procReq);
        
        requestService.addSentRequest(storageRequest);
        try {
            requestProducer.sendRequest(storageRequest, propertyManager.getProperty(Properties.KAFKA_TOPIC_STORAGE_REQUEST));
        } catch (MessageCreationException e) {
            // FIXME: send to monitor app
            messageHandler.handleMessage("Could not send message.", e, MessageType.ERROR);
        }
    }
    
    /**

    Sends a storage request for the specified file.
    @param file The file to be stored.
    @param downloadPath The path where the file should be downloaded.
    @param downloadUrl The URL for downloading the file.
    @param type The type of the file.
    @param pageNr The page number within the document.
    */
    protected void sendStorageRequest(IFile file, String downloadPath, String downloadUrl, FileType type, int pageNr) {
        IStorageRequest storageRequest;
        try {
            storageRequest = requestHelper.createStorageRequest(file, downloadPath, downloadUrl, type, fileService.generateRequestId(REQUEST_PREFIX), pageNr);
        } catch (GilesProcessingException e) {
            // should not happen
            // FIXME: send to monitor app
            messageHandler.handleMessage("Could not create request.", e, MessageType.ERROR);
            return;
        }
        
        IProcessingRequest procReq = new ProcessingRequest();
        procReq.setDocumentId(file.getDocumentId());
        procReq.setFileId(file.getId());
        procReq.setSentRequest(storageRequest);
        procReq.setRequestId(storageRequest.getRequestId());
        processingRequestService.saveNewProcessingRequest(procReq);
        
        requestService.addSentRequest(storageRequest);
        try {
            requestProducer.sendRequest(storageRequest, propertyManager.getProperty(Properties.KAFKA_TOPIC_STORAGE_REQUEST));
        } catch (MessageCreationException e) {
            // FIXME: send to monitor app
            messageHandler.handleMessage("Could not send message.", e, MessageType.ERROR);
        }
    }
    
    protected IFile createFile(IFile file, IDocument document, String contentType, long size, String filename, String requestPrefix) {
        IFile pagefile = new File();
        pagefile.setAccess(document.getAccess());
        pagefile.setContentType(contentType);
        pagefile.setDerivedFrom(file.getId());
        pagefile.setDocumentId(document.getId());
        pagefile.setUploadId(file.getUploadId());
        pagefile.setUploadDate(OffsetDateTime.now(ZoneId.of("UTC")).toString());
        pagefile.setFilename(filename);
        pagefile.setRequestId(fileService.generateRequestId(requestPrefix));
        pagefile.setUsername(document.getUsername());
        pagefile.setUsernameForStorage(requestHelper.getUsernameForStorage(userManager.findUser(document.getUsername())));
        pagefile.setProcessingStatus(ProcessingStatus.AWAITING_STORAGE);
        pagefile.setSize(size);
        pagefile.setId(fileService.generateFileId());
        
        return pagefile;
    }
}
