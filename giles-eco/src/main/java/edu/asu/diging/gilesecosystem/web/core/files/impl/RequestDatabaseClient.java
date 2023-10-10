package edu.asu.diging.gilesecosystem.web.core.files.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.web.core.files.IRequestDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.repository.RequestRepository;

@Transactional
@Component
public class RequestDatabaseClient implements IRequestDatabaseClient {
    
    @Autowired
    private RequestRepository requestRepository;
    
    public void deleteRequestsByDocumentId(String documentId) {
        requestRepository.deleteByDocumentId(documentId);
    }
}
