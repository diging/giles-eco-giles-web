package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.IStorageRequest;
import edu.asu.diging.gilesecosystem.requests.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.requests.kafka.IRequestProducer;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IPage;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.impl.File;
import edu.asu.diging.gilesecosystem.web.core.impl.Page;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.processing.helpers.RequestHelper;
import edu.asu.diging.gilesecosystem.web.service.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.users.IUserManager;

public abstract class ACompletedExtractionProcessor {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    protected IPropertiesManager propertyManager;
   
    @Autowired
    protected IRequestProducer requestProducer; 
    
    @Autowired
    protected RequestHelper requestHelper;
    
    @Autowired
    private IFilesDatabaseClient filesDbClient;
     
    @Autowired
    private IUserManager userManager;
    

    protected void sendRequest(IFile file, String downloadPath, String downloadUrl, FileType type) {
        IStorageRequest storageRequest;
        try {
            storageRequest = requestHelper.createStorageRequest(file, downloadPath, downloadUrl, type);
        } catch (GilesProcessingException e) {
            // should not happen
            // FIXME: send to monitor app
            logger.error("Could not create request.", e);
            return;
        }
        try {
            requestProducer.sendRequest(storageRequest, propertyManager.getProperty(IPropertiesManager.KAFKA_TOPIC_STORAGE_REQUEST));
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
