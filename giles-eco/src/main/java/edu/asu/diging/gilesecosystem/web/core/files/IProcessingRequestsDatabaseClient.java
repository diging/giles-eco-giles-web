package edu.asu.diging.gilesecosystem.web.core.files;

import java.util.List;

import edu.asu.diging.gilesecosystem.util.store.IDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;

public interface IProcessingRequestsDatabaseClient extends IDatabaseClient<IProcessingRequest> {

    List<IProcessingRequest> getRequestByDocumentId(String docId);

    public abstract void saveNewRequest(IProcessingRequest request);

    public abstract List<IProcessingRequest> getProcRequestsByRequestId(String procReqId);

    public abstract List<IProcessingRequest> getIncompleteRequests();
    
    public abstract void saveRequest(IProcessingRequest request);
}
