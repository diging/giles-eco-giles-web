package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.processing.ICompletedStorageRequestProcessor;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingCoordinator;

@Service
public class CompletedStorageRequestProcessor implements ICompletedStorageRequestProcessor {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("tmpStorageManager") 
    private IFileStorageManager storageManager;

    @Autowired
    private IFilesDatabaseClient filesDbClient;
    
    @Autowired
    private IProcessingCoordinator processCoordinator;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.processing.impl.ICompletedStorageRequestProcessor#processCompletedRequest(edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest)
     */
    @Override
    public void processCompletedRequest(ICompletedStorageRequest request) {
        IFile file = filesDbClient.getFileByRequestId(request.getRequestId());
        
        file.setStorageId(request.getFileId());
        file.setDownloadUrl(request.getDownloadUrl());
        file.setProcessingStatus(ProcessingStatus.STORED);
        file.setFilepath(request.getPathToFile());
        
        try {
            filesDbClient.saveFile(file);
        } catch (UnstorableObjectException e) {
            logger.error("Could not store file.", e);
            // fail silently...
            // this should never happen
        }
        
        try {
            processCoordinator.processFile(file, null);
        } catch (GilesProcessingException e) {
            //FIXME: this should go in a monitoring app
            logger.error("Exception occured in next processing phase.", e);
        }
    }
}
