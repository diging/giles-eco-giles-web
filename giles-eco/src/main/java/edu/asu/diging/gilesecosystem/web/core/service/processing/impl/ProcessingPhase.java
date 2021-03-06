package edu.asu.diging.gilesecosystem.web.core.service.processing.impl;

import org.springframework.beans.factory.annotation.Autowired;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.requests.kafka.IRequestProducer;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.model.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.model.impl.ProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.service.IProcessingRequestService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalProcessingRequestService;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IProcessingCoordinator;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IProcessingInfo;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IProcessingPhase;

public abstract class ProcessingPhase<T extends IProcessingInfo> implements IProcessingPhase<T> {

    @Autowired 
    private ITransactionalDocumentService documentService;
    
    @Autowired
    private ITransactionalFileService filesService;
    
    @Autowired
    private ITransactionalProcessingRequestService processingRequestService;
    
    @Autowired
    private IRequestProducer requestProducer;  
    
    @Autowired
    private IProcessingCoordinator processCoordinator;

    @Autowired
    private ISystemMessageHandler messageHandler;
    
    @Autowired
    private IProcessingRequestService requestService;
    
    public RequestStatus process(IFile file, IProcessingInfo info)
            throws GilesProcessingException {
        
        IRequest request;
        try {
            request = createRequest(file, info);
        } catch (GilesProcessingException ex) {
            messageHandler.handleMessage("Could not create request.", ex, MessageType.ERROR);
            return RequestStatus.FAILED;
        }
        
        if (request == null) {
            file.setProcessingStatus(getCompletedStatus());
            try {
                filesService.saveFile(file);
            } catch (UnstorableObjectException e) {
                throw new GilesProcessingException(e);
            }
            try {
                RequestStatus status = processCoordinator.processFile(file, null);
                postProcessing(file);
                return status;
            } catch (GilesProcessingException e) {
                //FIXME: this should go in a monitoring app
                messageHandler.handleMessage("Exception occured in next processing phase.", e, MessageType.ERROR);
            }
        }
        
        IDocument document = documentService.getDocument(file.getDocumentId());
        
        IProcessingRequest procReq = new ProcessingRequest();
        procReq.setRequestId(request.getRequestId());
        procReq.setDocumentId(document.getId());
        procReq.setFileId(file.getId());
        procReq.setSentRequest(request);
        procReq.setRequestStatus(request.getStatus());
        processingRequestService.saveNewProcessingRequest(procReq);
        
        try {
            documentService.saveDocument(document);
        } catch (UnstorableObjectException e1) {
            throw new GilesProcessingException(e1);
        }     
        
        sendRequest(request, document);
        
        request.setStatus(RequestStatus.SUBMITTED);
        try {
            documentService.saveDocument(document);
        } catch (UnstorableObjectException e) {
            throw new GilesProcessingException(e);
        }
        
        return request.getStatus();
    }

    public void sendRequest(IRequest request, IDocument document)
            throws GilesProcessingException {
        requestService.addSentRequest(request);
        try {
            requestProducer.sendRequest(request, getTopic());
        } catch (MessageCreationException e) {
            request.setStatus(RequestStatus.FAILED);
            try {
                documentService.saveDocument(document);
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
