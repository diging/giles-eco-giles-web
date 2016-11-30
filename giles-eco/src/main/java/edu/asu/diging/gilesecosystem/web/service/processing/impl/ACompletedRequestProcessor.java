package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.files.IProcessingRequestsDatabaseClient;

public abstract class ACompletedRequestProcessor {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IProcessingRequestsDatabaseClient pReqDbClient;
    
    protected void markRequestComplete(IRequest completeRequest) {
        List<IProcessingRequest> pRequests = pReqDbClient.getRequestByDocumentId(completeRequest.getRequestId());
        for (IProcessingRequest pReq : pRequests) {
            pReq.setCompletedRequest(completeRequest);
            pReq.setRequestStatus(completeRequest.getStatus());
            try {
                pReqDbClient.store(pReq);
            } catch (UnstorableObjectException e) {
                // should never happen
                // FIXME send to monitoring app
                logger.error("Could not store request.", e);
            }
        }
    }
    
     
   
}
