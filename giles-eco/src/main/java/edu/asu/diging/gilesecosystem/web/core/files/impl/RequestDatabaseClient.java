package edu.asu.diging.gilesecosystem.web.core.files.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.util.store.objectdb.DatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.files.IRequestDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.repository.RequestRepository;

@Transactional
@Component
public class RequestDatabaseClient extends DatabaseClient<IRequest> implements IRequestDatabaseClient {
    
    @PersistenceContext(unitName="entityManagerFactory")
    private EntityManager em;
    
    @Autowired
    private final RequestRepository requestRepository;
    
    @Autowired
    public RequestDatabaseClient(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }
    
    public void deleteRequestsByDocumentId(String documentId) {
        requestRepository.deleteByDocumentId(documentId);
    }
}
