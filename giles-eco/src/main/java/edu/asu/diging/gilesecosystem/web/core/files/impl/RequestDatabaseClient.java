package edu.asu.diging.gilesecosystem.web.core.files.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.asu.diging.gilesecosystem.web.core.files.IRequestDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.repository.RequestRepository;

@Component
public class RequestDatabaseClient implements IRequestDatabaseClient {
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
