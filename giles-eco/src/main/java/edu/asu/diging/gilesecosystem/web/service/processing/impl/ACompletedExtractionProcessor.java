package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.IStorageRequest;
import edu.asu.diging.gilesecosystem.requests.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.requests.kafka.IRequestProducer;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.impl.File;
import edu.asu.diging.gilesecosystem.web.core.impl.ProcessingRequest;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.files.IProcessingRequestsDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.processing.helpers.RequestHelper;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.users.IUserManager;

public abstract class ACompletedExtractionProcessor extends ACompletedRequestProcessor {

    public final static String REQUEST_PREFIX = "STDERREQ";

    @Autowired
    protected IPropertiesManager propertyManager;
   
    @Autowired
    protected IRequestProducer requestProducer; 
    
    @Autowired
    protected RequestHelper requestHelper;
    
    @Autowired
    private IFilesDatabaseClient filesDbClient;
    
    @Autowired
    private IProcessingRequestsDatabaseClient pReqDbClient;
     
    @Autowired
    private IUserManager userManager;
    

    protected void sendRequest(IFile file, String downloadPath, String downloadUrl, FileType type) {
        IStorageRequest storageRequest;
        try {
            storageRequest = requestHelper.createStorageRequest(file, downloadPath, downloadUrl, type, filesDbClient.generateId(REQUEST_PREFIX, filesDbClient::getFileByRequestId));
        } catch (GilesProcessingException e) {
            // should not happen
            // FIXME: send to monitor app
            logger.error("Could not create request.", e);
            return;
        }
        
        IProcessingRequest procReq = new ProcessingRequest();
        procReq.setDocumentId(file.getDocumentId());
        procReq.setFileId(file.getId());
        procReq.setSentRequest(storageRequest);
        procReq.setRequestId(storageRequest.getRequestId());
        pReqDbClient.saveNewRequest(procReq);
        
        try {
            requestProducer.sendRequest(storageRequest, propertyManager.getProperty(Properties.KAFKA_TOPIC_STORAGE_REQUEST));
        } catch (MessageCreationException e) {
            // FIXME: send to monitor app
            logger.error("Could not send message.", e);
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
        pagefile.setRequestId(filesDbClient.generateId(requestPrefix, filesDbClient::getFileByRequestId));
        pagefile.setUsername(document.getUsername());
        pagefile.setUsernameForStorage(requestHelper.getUsernameForStorage(userManager.findUser(document.getUsername())));
        pagefile.setProcessingStatus(ProcessingStatus.AWAITING_STORAGE);
        pagefile.setSize(size);
        pagefile.setId(filesDbClient.generateId());
        
        return pagefile;
    }
}
