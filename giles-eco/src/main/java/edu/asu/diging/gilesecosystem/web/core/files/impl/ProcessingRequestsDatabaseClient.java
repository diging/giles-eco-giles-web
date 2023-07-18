package edu.asu.diging.gilesecosystem.web.core.files.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.store.objectdb.DatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.files.IProcessingRequestsDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.model.impl.ProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.repository.ProcessingRequestRepository;

@Component
public class ProcessingRequestsDatabaseClient extends DatabaseClient<IProcessingRequest> implements IProcessingRequestsDatabaseClient {

    private final ProcessingRequestRepository processingRequestRepository;

    @Autowired
    public ProcessingRequestsDatabaseClient(ProcessingRequestRepository processingRequestRepository) {
        this.processingRequestRepository = processingRequestRepository;
    }
    
    @Autowired
    private ISystemMessageHandler messageHandler;

    @Override
    public List<IProcessingRequest> getRequestByDocumentId(String docId) {
        List<ProcessingRequest> processingRequests = processingRequestRepository.findByDocumentId(docId);
        return processingRequests.stream().map(processingRequest -> (IProcessingRequest) processingRequest).collect(Collectors.toList());
    }
    
    @Override
    public List<IProcessingRequest> getProcRequestsByRequestId(String procReqId) {
        List<ProcessingRequest> processingRequests = processingRequestRepository.findByRequestId(procReqId);
        return processingRequests.stream().map(processingRequest -> (IProcessingRequest) processingRequest).collect(Collectors.toList());
    }
    
    @Override
    public void saveNewRequest(IProcessingRequest request) {
        request.setId(generateId());
        try {
            processingRequestRepository.save((ProcessingRequest) request);
        } catch (IllegalArgumentException e) {
            messageHandler.handleMessage("Could not store element.", e, MessageType.ERROR);
        }
    }
    
    @Override
    public void saveRequest(IProcessingRequest request) {
        try {
            processingRequestRepository.save((ProcessingRequest) request);
        } catch (IllegalArgumentException e) {
            messageHandler.handleMessage("Could not store element.", e, MessageType.ERROR);
        }
    }
    
    @Override
    public List<IProcessingRequest> getIncompleteRequests() {
        List<ProcessingRequest> processingRequests = processingRequestRepository.findByCompletedRequestIsNull();
        return processingRequests.stream().map(processingRequest -> (IProcessingRequest) processingRequest).collect(Collectors.toList());
    }
    
    @Override
    protected IProcessingRequest getById(String id) {
        return (IProcessingRequest) processingRequestRepository.findById(id).orElse(null);
    }
    
    @Override
    protected String getIdPrefix() {
        return "PREQ";
    }

    @Override
    protected EntityManager getClient() {
       return null;
    }
}
