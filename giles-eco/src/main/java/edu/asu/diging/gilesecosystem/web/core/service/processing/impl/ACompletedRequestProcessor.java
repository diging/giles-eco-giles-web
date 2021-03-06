package edu.asu.diging.gilesecosystem.web.core.service.processing.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalProcessingRequestService;

public abstract class ACompletedRequestProcessor {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ITransactionalProcessingRequestService pReqDbClient;

    @Autowired
    private ISystemMessageHandler messageHandler;
    
    protected void markRequestComplete(IRequest completeRequest) {
        List<IProcessingRequest> pRequests = pReqDbClient.getProcRequestsByRequestId(completeRequest.getRequestId());
        for (IProcessingRequest pReq : pRequests) {
            pReq.setCompletedRequest(completeRequest);
            pReq.setRequestStatus(completeRequest.getStatus());
            try {
                pReqDbClient.save(pReq);
            } catch (UnstorableObjectException e) {
                // should never happen
                // FIXME send to monitoring app
                messageHandler.handleMessage("Could not store request.", e, MessageType.ERROR);
            }
        }
    }
    
     
   
}
