package edu.asu.diging.gilesecosystem.web.core.files;

import java.util.List;

import edu.asu.diging.gilesecosystem.util.store.IDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;

public interface IProcessingRequestsDatabaseClient extends IDatabaseClient<IProcessingRequest> {

    List<IProcessingRequest> getRequestByDocumentId(String docId);

    public abstract void saveNewRequest(IProcessingRequest request);

    public abstract List<IProcessingRequest> getProcRequestsByRequestId(String procReqId);

    public abstract List<IProcessingRequest> getIncompleteRequests();
    
    /**
    Deletes processing requests associated with the specified document ID.
    @param documentId The ID of the document for which processing requests should be deleted.
    */
    public abstract void deleteRequestsByDocumentId(String documentId);

}
