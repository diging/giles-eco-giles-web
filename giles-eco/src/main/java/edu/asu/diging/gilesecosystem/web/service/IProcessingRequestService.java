package edu.asu.diging.gilesecosystem.web.service;

import java.util.List;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.web.service.impl.TimestampedRequestData;

public interface IProcessingRequestService {

    /**
     * Method to append new requests. If there are more than the maximum number of
     * current requests in the queue (as specified in the config file), the first one
     * is removed from the queue before adding the new one.
     * 
     * This method is not thread-safe as it is ok if there are a few too many or too few 
     * elements in the queue. 
     * 
     * @param request Request to be added to current queue.
     */
    void addReceivedRequest(IRequest request);

    List<TimestampedRequestData> getCurrentReceivedRequests();

    List<TimestampedRequestData> getCurrentSentRequests();

    void addSentRequest(IRequest request);

}