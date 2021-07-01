package edu.asu.diging.gilesecosystem.web.core.service;

import java.util.List;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.web.core.service.impl.TimestampedRequestData;

/**
 * Service that holds recent processing requests (sent and received). 
 * 
 * @author jdamerow
 *
 */
public interface IProcessingRequestService {

    /**
     * Method to append new requests. If there are more than the maximum number of
     * current requests in the queue (as specified in the config file), the first one
     * is removed from the queue before adding the new one.
     * 
     * This method does not have to be thread-safe as it is ok if there are a few too 
     * many or too few elements in the queue. 
     * 
     * @param request Request to be added to current queue.
     */
    void addReceivedRequest(IRequest request);

    /**
     * Returns recently received requests as a list.
     * 
     * @return List of recently received requests with the oldest request first.
     */
    List<TimestampedRequestData> getCurrentReceivedRequests();

    /**
     * Returns recently sent requests as a list.
     * 
     * @return List of recently sent requests with the oldest request first.
     */
    List<TimestampedRequestData> getCurrentSentRequests();

    /** 
     * Method to add a new request to the sent queue. If there are more than the maximum number of
     * current requests in the queue (as specified in the config file), the first one
     * is removed from the queue before adding the new one.
     * 
     * @param request Sent request to be added to the queue.
     */
    void addSentRequest(IRequest request);

}