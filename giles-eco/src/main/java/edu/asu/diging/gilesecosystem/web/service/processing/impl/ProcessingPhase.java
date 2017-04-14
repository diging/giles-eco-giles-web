package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.requests.kafka.IRequestProducer;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.impl.ProcessingRequest;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.files.IDocumentDatabaseClient;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.files.IProcessingRequestsDatabaseClient;
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
    private IProcessingRequestsDatabaseClient pReqDbClient;
    
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
                postProcessing(file);
                return status;
            } catch (GilesProcessingException e) {
                //FIXME: this should go in a monitoring app
                logger.error("Exception occured in next processing phase.", e);
            }
        }
        
        IDocument document = documentsDbClient.getDocumentById(file.getDocumentId());
        document.setRequest(request);
        
        IProcessingRequest procReq = new ProcessingRequest();
        procReq.setRequestId(request.getRequestId());
        procReq.setDocumentId(document.getId());
        procReq.setFileId(file.getId());
        procReq.setSentRequest(request);
        procReq.setRequestStatus(request.getStatus());
        pReqDbClient.saveNewRequest(procReq);
        
        try {
            documentsDbClient.saveDocument(document);
        } catch (UnstorableObjectException e1) {
            throw new GilesProcessingException(e1);
        }     
        
        sendRequest(request, document);
        
        request.setStatus(RequestStatus.SUBMITTED);
        try {
            documentsDbClient.saveDocument(document);
        } catch (UnstorableObjectException e) {
            throw new GilesProcessingException(e);
        }
        
        return request.getStatus();
    }

    public void sendRequest(IRequest request, IDocument document)
            throws GilesProcessingException {
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
    }
    
    protected abstract IRequest createRequest(IFile file, IProcessingInfo info) throws GilesProcessingException ;
    
    protected abstract String getTopic();
    
    protected abstract ProcessingStatus getCompletedStatus();
    
    protected abstract void postProcessing(IFile file);
    
    public abstract Class<? extends IRequest> getSupportedRequestType();
}
