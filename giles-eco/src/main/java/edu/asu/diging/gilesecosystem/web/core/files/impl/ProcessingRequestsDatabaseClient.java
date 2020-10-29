package edu.asu.diging.gilesecosystem.web.core.files.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.objectdb.DatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.files.IProcessingRequestsDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.model.impl.ProcessingRequest;

@Transactional
@Component
public class ProcessingRequestsDatabaseClient extends DatabaseClient<IProcessingRequest> implements IProcessingRequestsDatabaseClient {

    @PersistenceContext(unitName="entityManagerFactory")
    private EntityManager em;
    
    @Autowired
    private ISystemMessageHandler messageHandler;

    @Override
    public List<IProcessingRequest> getRequestByDocumentId(String docId) {
        List<Object> results = getClient().createQuery("SELECT t FROM " + IProcessingRequest.class.getName()  + " t WHERE t.documentId = '" + docId + "'").getResultList();
        TypedQuery<IProcessingRequest> query = getClient().createQuery("SELECT t FROM " + IProcessingRequest.class.getName()  + " t WHERE t.documentId = '" + docId + "'", IProcessingRequest.class);
        return query.getResultList();
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
            messageHandler.handleMessage("Could not store element.", e, MessageType.ERROR);
        }
    }
    
    @Override
    public List<IProcessingRequest> getIncompleteRequests() {
        List<IProcessingRequest> results = new ArrayList<IProcessingRequest>();
        TypedQuery<ProcessingRequest> query = getClient().createQuery("SELECT t FROM " + ProcessingRequest.class.getName()  + " t WHERE t.completedRequest IS NULL", ProcessingRequest.class);
        query.getResultList().forEach(x -> results.add(x));
        return results;
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
