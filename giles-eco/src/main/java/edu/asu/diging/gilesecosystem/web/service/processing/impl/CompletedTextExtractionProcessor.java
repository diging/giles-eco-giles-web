package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.ICompletedTextExtractionRequest;
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
import edu.asu.diging.gilesecosystem.web.files.IDocumentDatabaseClient;
import edu.asu.diging.gilesecosystem.web.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.processing.ICompletedTextExtractionProcessor;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingCoordinator;
import edu.asu.diging.gilesecosystem.web.service.processing.helpers.RequestHelper;
import edu.asu.diging.gilesecosystem.web.service.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.users.IUserManager;

@Service
public class CompletedTextExtractionProcessor implements ICompletedTextExtractionProcessor {
    
    public final static String REQUEST_PREFIX = "TXTREQ";
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("tmpStorageManager") 
    private IFileStorageManager storageManager;

    @Autowired
    private IDocumentDatabaseClient docsDbClient;
    
    @Autowired
    private IFilesDatabaseClient filesDbClient;
    
    @Autowired
    private IPropertiesManager propertyManager;
    
    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private IRequestProducer requestProducer; 
    
    @Autowired
    private RequestHelper requestHelper;
    
    @Autowired
    private IProcessingCoordinator processCoordinator;


    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.processing.impl.ICompletedTextExtractionProcessor#processCompletedRequest(edu.asu.diging.gilesecosystem.requests.ICompletedTextExtractionRequest)
     */
    @Override
    public void processCompletedRequest(ICompletedTextExtractionRequest request) {
        IDocument document = docsDbClient.getDocumentById(request.getDocumentId());
        IFile file = filesDbClient.getFileById(document.getUploadedFileId());
        
        String completeTextDownload = request.getDownloadUrl();
        // text was extracted
        if (completeTextDownload != null && !completeTextDownload.isEmpty()) {
            IFile completeText = new File();
            completeText.setAccess(document.getAccess());
            completeText.setContentType(MediaType.TEXT_PLAIN_VALUE);
            completeText.setDerivedFrom(file.getId());
            completeText.setDocumentId(document.getId());
            completeText.setFilename(request.getTextFilename());
            completeText.setRequestId(filesDbClient.generateId(REQUEST_PREFIX, filesDbClient::getFileByRequestId));
            completeText.setUsername(document.getUsername());
            completeText.setUsernameForStorage(requestHelper.getUsernameForStorage(userManager.findUser(document.getUsername())));
            completeText.setProcessingStatus(ProcessingStatus.AWAITING_STORAGE);
            completeText.setId(filesDbClient.generateId());
            try {
                filesDbClient.saveFile(completeText);
            } catch (UnstorableObjectException e) {
                // should never happen, we're setting the id
                logger.error("Could not store file.", e);
            }
            
            document.setExtractedTextFileId(completeText.getId());
            
            sendRequest(completeText, request.getDownloadPath(), request.getDownloadUrl());
        }
        
        if (request.getPages() != null ) {
            for (edu.asu.diging.gilesecosystem.requests.impl.Page page : request.getPages()) {
                IFile pageText = new File();
                pageText.setAccess(document.getAccess());
                pageText.setContentType(MediaType.TEXT_PLAIN_VALUE);
                pageText.setDerivedFrom(file.getId());
                pageText.setDocumentId(document.getId());
                pageText.setUploadId(file.getUploadId());
                pageText.setUploadDate(OffsetDateTime.now(ZoneId.of("UTC")).toString());
                pageText.setFilename(page.getFilename());
                pageText.setRequestId(filesDbClient.generateId(REQUEST_PREFIX, filesDbClient::getFileByRequestId));
                pageText.setUsername(document.getUsername());
                pageText.setUsernameForStorage(requestHelper.getUsernameForStorage(userManager.findUser(document.getUsername())));
                pageText.setProcessingStatus(ProcessingStatus.AWAITING_STORAGE);
                pageText.setId(filesDbClient.generateId());
                try {
                    filesDbClient.saveFile(pageText);
                } catch (UnstorableObjectException e) {
                    // should never happen, we're setting the id
                    logger.error("Could not store file.", e);
                }
                
                IPage documentPage = new Page();
                documentPage.setPageNr(page.getPageNr());
                documentPage.setTextFileId(pageText.getId());
                
                document.getPages().add(documentPage);
                
                sendRequest(pageText, request.getDownloadPath(), request.getDownloadUrl());
            }
        }
        
        file.setProcessingStatus(ProcessingStatus.TEXT_EXTRACTION_COMPLETE);
        
        try {
            docsDbClient.saveDocument(document);
        } catch (UnstorableObjectException e) {
            // shoudl never happen
            // report to monitoring app
            logger.error("Could not store document.", e);
        }
        
        try {
            processCoordinator.processFile(file, null);
        } catch (GilesProcessingException e) {
            // FIXME: send to monitoring app
            logger.error("Processing failed.", e);
        }
    }


    private void sendRequest(IFile file, String downloadPath, String downloadUrl) {
        IStorageRequest storageRequest;
        try {
            storageRequest = requestHelper.createStorageRequest(file, downloadPath, downloadUrl, FileType.TEXT);
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
}
