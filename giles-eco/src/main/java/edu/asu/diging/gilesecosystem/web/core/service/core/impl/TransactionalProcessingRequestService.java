package edu.asu.diging.gilesecosystem.web.core.service.core.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.files.IProcessingRequestsDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalProcessingRequestService;

@Service
@Transactional("transactionManager")
public class TransactionalProcessingRequestService implements ITransactionalProcessingRequestService {

    @Autowired
    private IProcessingRequestsDatabaseClient pReqDbClient;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.core.impl.ITransactionalProcessingRequestService#saveProcessingRequest(edu.asu.diging.gilesecosystem.web.core.IProcessingRequest)
     */
    @Override
    public void saveNewProcessingRequest(IProcessingRequest request) {
        pReqDbClient.saveNewRequest(request);
    }
    
    @Override
    public void save(IProcessingRequest request) throws UnstorableObjectException {
        pReqDbClient.store(request);
    }
    
    @Override
    public List<IProcessingRequest> getProcRequestsByRequestId(String requestId) {
        return pReqDbClient.getProcRequestsByRequestId(requestId);
    }
    
    @Override
    public List<IProcessingRequest> getIncompleteRequests() {
        return pReqDbClient.getIncompleteRequests();
    }
    
    @Override
    public void deleteProcessingRequestsForDocumentId(String documentId) {
        pReqDbClient.deleteProcessingRequestsForDocumentId(documentId);
    }
}
