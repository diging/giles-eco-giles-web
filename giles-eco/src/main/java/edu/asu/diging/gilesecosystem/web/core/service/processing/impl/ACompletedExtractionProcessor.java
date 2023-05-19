package edu.asu.diging.gilesecosystem.web.core.service.processing.impl;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.IStorageRequest;
import edu.asu.diging.gilesecosystem.requests.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.requests.kafka.IRequestProducer;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IPage;
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
    
    @Autowired
    private ITransactionalFileService filesService;

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
    
    /**
     * 
     * This method will return a file if it's already present in case of reprocessing else it will create a new file
     * @param ipage : The document page being processed
     * @param document : The document associated with the page
     * @param file : The file associated with the page
     * @param contentType : The content type of the file
     * @param size : the size of the file
     * @param fileName : the file name
     * @param requestPrefix : the request prefix for example text request will have TXTREQ
     * @param getFileIdFunction : the function that returns the required file id
     * @return existing file or newly created file
     */
    protected IFile getFile(IPage documentPage, IDocument document, IFile file, String contentType, long size, String fileName, String requestPrefix, Function<IPage, String> getFileIdFunction) {
        if(documentPage != null) {
            String fileId = getFileIdFunction.apply(documentPage);
            if (fileId != null && !fileId.isEmpty()) {
                return filesService.getFileById(fileId);
            }
        }    
        return createNewFile(file, document, contentType, size, fileName, requestPrefix);
    }
    /**
     * 
     * This method will return a file if it's already present in case of reprocessing else it will create a new file
     * @param file : The file associated with the page
     * @param document : The document associated with the page
     * @param contentType : The content type of the file
     * @param size : the size of the file
     * @param fileName : the file name
     * @param requestPrefix : the request prefix for example text request will have TXTREQ
     * @return existing file or newly created file
     */
    protected IFile getFile(IFile file, IDocument document, String contentType, long size, String fileName, String requestPrefix, Function<IDocument, String> getFileIdFunction) {
        String fileId = getFileIdFunction.apply(document);
        if (fileId != null && !fileId.isEmpty()) {
            return filesService.getFileById(fileId);
        }
        return createNewFile(file, document, contentType, size, fileName, requestPrefix);
    }
    
    private IFile createNewFile(IFile file, IDocument document, String contentType, long size, String fileName, String requestPrefix) {
        IFile newFile = createFile(file, document, contentType, size, fileName, requestPrefix);
        try {
            filesService.saveFile(newFile);
        } catch (UnstorableObjectException e) {
            // should never happen, we're setting the id
            messageHandler.handleMessage("Could not store file.", e, MessageType.ERROR);
        }
        return newFile;
    }
}
