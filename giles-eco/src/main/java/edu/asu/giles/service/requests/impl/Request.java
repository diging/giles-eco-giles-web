package edu.asu.giles.service.requests.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.asu.giles.service.requests.IRequest;
import edu.asu.giles.service.requests.RequestStatus;

/**
 * Base class for all requests to be submitted to Kafka.
 * 
 * @author jdamerow
 *
 */
public class Request implements IRequest {

    @JsonProperty
    private String requestType;

    @JsonProperty
    private String uploadId;
    
    @JsonProperty
    private String documentId;
    
    @JsonIgnore
    private RequestStatus status;
    
    /* (non-Javadoc)
     * @see edu.asu.giles.service.requests.impl.IRequest#getUploadId()
     */
    @Override
    public String getUploadId() {
        return uploadId;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.service.requests.impl.IRequest#setUploadId(java.lang.String)
     */
    @Override
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.service.requests.impl.IRequest#getStatus()
     */
    @Override
    public RequestStatus getStatus() {
        return status;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.service.requests.impl.IRequest#setStatus(edu.asu.giles.service.requests.impl.RequestStatus)
     */
    @Override
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
    
    @Override
    public String getDocumentId() {
        return documentId;
    }
    @Override
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    @Override
    public String getRequestType() {
        return requestType;
    }
    @Override
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
    
}
