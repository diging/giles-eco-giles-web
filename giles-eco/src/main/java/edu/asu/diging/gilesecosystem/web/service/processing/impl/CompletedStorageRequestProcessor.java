package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.impl.CompletedStorageRequest;
import edu.asu.diging.gilesecosystem.septemberutil.service.impl.SystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.domain.IFile;
import edu.asu.diging.gilesecosystem.web.domain.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.service.processing.ICompletedStorageRequestProcessor;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingCoordinator;
import edu.asu.diging.gilesecosystem.web.service.processing.RequestProcessor;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;

@Service
public class CompletedStorageRequestProcessor extends ACompletedRequestProcessor implements RequestProcessor<ICompletedStorageRequest>, ICompletedStorageRequestProcessor {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("tmpStorageManager") 
    private IFileStorageManager storageManager;

    @Autowired
    private ITransactionalFileService filesService;
    
    @Autowired
    private IProcessingCoordinator processCoordinator;
    
    @Autowired
    private IPropertiesManager propertiesManager;
   
    @Autowired
    private SystemMessageHandler messageHandler;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.processing.impl.ICompletedStorageRequestProcessor#processCompletedRequest(edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest)
     */
    @Override
    public void processRequest(ICompletedStorageRequest request) {
        IFile file = filesService.getFileById(request.getFileId());
        
        file.setStorageId(request.getStoredFileId());
        file.setDownloadUrl(request.getDownloadUrl());
        if (request.getDownloadUrl() != null && !request.getDownloadUrl().isEmpty()) {
            request.setStatus(RequestStatus.COMPLETE);
        } else {
            request.setStatus(RequestStatus.FAILED);
        }
        file.setProcessingStatus(ProcessingStatus.STORED);
        file.setFilepath(request.getDownloadPath());
        
        markRequestComplete(request);
        
        try {
            filesService.saveFile(file);
        } catch (UnstorableObjectException e) {
            messageHandler.handleError("Could not store file.", e);
            // fail silently...
            // this should never happen
        }
        
        try {
            processCoordinator.processFile(file, null);
        } catch (GilesProcessingException e) {
            //FIXME: this should go in a monitoring app
            messageHandler.handleError("Exception occured in next processing phase.", e);
        }
    }

    @Override
    public String getProcessedTopic() {
        return propertiesManager.getProperty(Properties.KAFKA_TOPIC_STORAGE_COMPLETE_REQUEST);
    }

    @Override
    public Class<? extends ICompletedStorageRequest> getRequestClass() {
        return CompletedStorageRequest.class;
    }
}
