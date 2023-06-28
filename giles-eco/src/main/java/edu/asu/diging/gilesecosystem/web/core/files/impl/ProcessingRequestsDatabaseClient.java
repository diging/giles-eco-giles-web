package edu.asu.diging.gilesecosystem.web.core.files.impl;
import java.util.List;

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
        return processingRequestRepository.findByDocumentId(docId);
    }
    
    @Override
    public List<IProcessingRequest> getProcRequestsByRequestId(String procReqId) {
        return processingRequestRepository.findByRequestId(procReqId);
    }
    
    @Override
    public void saveNewRequest(IProcessingRequest request) {
        request.setId(generateId());
        try {
            processingRequestRepository.save((ProcessingRequest) request);
        } catch (IllegalArgumentException e) {
            // should never happen
            messageHandler.handleMessage("Could not store element.", e, MessageType.ERROR);
        }
    }
    
    @Override
    public List<IProcessingRequest> getIncompleteRequests() {
        return processingRequestRepository.findByCompletedRequestIsNull();
    }
    
    @Override
    protected String getIdPrefix() {
        return "PREQ";
    }

    @Override
    protected IProcessingRequest getById(String id) {
        return processingRequestRepository.findById(id).orElse(null);
    }

    @Override
    protected EntityManager getClient() {
        return null;
    }
}
