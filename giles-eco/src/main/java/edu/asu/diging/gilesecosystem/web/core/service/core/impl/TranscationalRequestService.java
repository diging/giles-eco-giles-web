package edu.asu.diging.gilesecosystem.web.core.service.core.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.web.core.files.IRequestDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalRequestService;

@Service
public class TranscationalRequestService implements ITransactionalRequestService {
    @Autowired
    private IRequestDatabaseClient requestDatabaseClient;
    
    @Override
    public void deleteRequestsByDocumentId(String documentId) {
        requestDatabaseClient.deleteRequestsByDocumentId(documentId);
    }
}
