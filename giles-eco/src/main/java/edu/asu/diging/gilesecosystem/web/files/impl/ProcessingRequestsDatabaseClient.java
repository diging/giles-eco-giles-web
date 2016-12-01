package edu.asu.diging.gilesecosystem.web.files.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.objectdb.DatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.impl.ProcessingRequest;
import edu.asu.diging.gilesecosystem.web.files.IProcessingRequestsDatabaseClient;

@Transactional("txmanager_data")
@Component
public class ProcessingRequestsDatabaseClient extends DatabaseClient<IProcessingRequest> implements IProcessingRequestsDatabaseClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @PersistenceContext(unitName="DataPU")
    private EntityManager em;
    
    @Override
    public List<IProcessingRequest> getRequestByDocumentId(String docId) {
        return searchByProperty("documentId", docId, ProcessingRequest.class);
    }
    
    @Override
    public List<IProcessingRequest> getProcRequestsByRequestId(String procReqId) {
        return searchByProperty("requestId", procReqId, ProcessingRequest.class);
    }
    
    @Override
    public void saveNewRequest(IProcessingRequest request) {
        request.setId(generateId());
        try {
            store(request);
        } catch (UnstorableObjectException e) {
            // should never happen
            logger.error("Could not store element.", e);
        }
    }
    
    @Override
    protected String getIdPrefix() {
        return "PREQ";
    }

    @Override
    protected IProcessingRequest getById(String id) {
        return em.find(ProcessingRequest.class, id);
    }

    @Override
    protected EntityManager getClient() {
       return em;
    }

}
