package edu.asu.giles.service.requests;

/**
 * This enum holds values to specify the status of a request.
 * <ul>
 *  <li>NEW: a request was created but not yet submitted to Kafka.</li>
 *  <li>SUBMITTED: a request was submitted to Kafka but not yet completed.</li>
 *  <li>COMPLETE: Giles got a response that request was succesfully completed.</li>
 *  <li>FAILED: the request could not be completed and Giles received an error response.</li>
 * </ul>
 * 
 * @author jdamerow
 *
 */
public enum RequestStatus {
    NEW,
    SUBMITTED,
    COMPLETE,
    FAILED
}
