package edu.asu.diging.gilesecosystem.web.core.service.core;

import java.util.List;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;

public interface ITransactionalProcessingRequestService {

    void saveNewProcessingRequest(IProcessingRequest request);

    List<IProcessingRequest> getProcRequestsByRequestId(String requestId);

    void save(IProcessingRequest request) throws UnstorableObjectException;

    List<IProcessingRequest> getIncompleteRequests();
    
    /**

    Deletes processing requests associated with the specified document ID.
    @param documentId The ID of the document for which processing requests should be deleted.
    */
    void deleteProcessingRequestsForDocumentId(String documentId);

}
