package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.requests.kafka.IRequestProducer;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.files.IDocumentDatabaseClient;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingCoordinator;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingInfo;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingPhase;

public abstract class ProcessingPhase<T extends IProcessingInfo> implements IProcessingPhase<T> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
     
    @Autowired 
    private IDocumentDatabaseClient documentsDbClient;
    
    @Autowired
    private IFilesDatabaseClient filesDbClient;
    
    @Autowired
    private IRequestProducer requestProducer;  
    
    @Autowired
    private IProcessingCoordinator processCoordinator;
    
    
    public RequestStatus process(IFile file, IProcessingInfo info)
            throws GilesProcessingException {
        
        IRequest request;
        try {
            request = createRequest(file, info);
        } catch (GilesProcessingException ex) {
            logger.error("Could not create request.", ex);
            return RequestStatus.FAILED;
        }
        
        if (request == null) {
            file.setProcessingStatus(getCompletedStatus());
            try {
                filesDbClient.saveFile(file);
            } catch (UnstorableObjectException e) {
                throw new GilesProcessingException(e);
            }
            try {
                RequestStatus status = processCoordinator.processFile(file, null);
                cleanup(file);
                return status;
            } catch (GilesProcessingException e) {
                //FIXME: this should go in a monitoring app
                logger.error("Exception occured in next processing phase.", e);
            }
        }
        
        IDocument document = documentsDbClient.getDocumentById(file.getDocumentId());
        document.setRequest(request);
        
        try {
            documentsDbClient.saveDocument(document);
        } catch (UnstorableObjectException e1) {
            throw new GilesProcessingException(e1);
        }     
        
        try {
            requestProducer.sendRequest(request, getTopic());
        } catch (MessageCreationException e) {
            request.setStatus(RequestStatus.FAILED);
            try {
                documentsDbClient.saveDocument(document);
            } catch (UnstorableObjectException e1) {
                throw new GilesProcessingException(e1);
            }
            throw new GilesProcessingException(e);
        }
        
        request.setStatus(RequestStatus.SUBMITTED);
        try {
            documentsDbClient.saveDocument(document);
        } catch (UnstorableObjectException e) {
            throw new GilesProcessingException(e);
        }
        
        return request.getStatus();
    }
    
    protected abstract IRequest createRequest(IFile file, IProcessingInfo info) throws GilesProcessingException ;
    
    protected abstract String getTopic();
    
    protected abstract ProcessingStatus getCompletedStatus();
    
    protected abstract void cleanup(IFile file);
}
