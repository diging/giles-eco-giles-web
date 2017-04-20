package edu.asu.diging.gilesecosystem.web.service.core;

import java.util.List;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.domain.IProcessingRequest;

public interface ITransactionalProcessingRequestService {

    void saveNewProcessingRequest(IProcessingRequest request);

    List<IProcessingRequest> getProcRequestsByRequestId(String requestId);

    void save(IProcessingRequest request) throws UnstorableObjectException;

    List<IProcessingRequest> getIncompleteRequests();

}