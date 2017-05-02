package edu.asu.diging.gilesecosystem.web.core.impl;

import javax.jdo.annotations.Index;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.web.core.IProcessingRequest;

/**
 * Storage class for request objects.
 * 
 * @deprecated
 *      Use {@link edu.asu.diging.gilesecosystem.web.domain.impl.ProcessingRequest} instead. This
 *      class is only kept for migration purposes.
 * 
 * @author jdamerow
 *
 */
@Deprecated
@Entity
public class ProcessingRequest implements IProcessingRequest {

    @Id private String id;
    @Index private String documentId;
    @Index private String fileId;
    @Index private String requestId;
    private RequestStatus requestStatus;
    @Basic(fetch = FetchType.EAGER) private IRequest sentRequest;
    @Basic(fetch = FetchType.EAGER) private IRequest completedRequest;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.core.impl.IProcessingRequests#getId()
     */
    @Override
    public String getId() {
        return id;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.core.impl.IProcessingRequests#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.core.impl.IProcessingRequests#getDocumentId()
     */
    @Override
    public String getDocumentId() {
        return documentId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.core.impl.IProcessingRequests#setDocumentId(java.lang.String)
     */
    @Override
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.core.impl.IProcessingRequests#getFileId()
     */
    @Override
    public String getFileId() {
        return fileId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.core.impl.IProcessingRequests#setFileId(java.lang.String)
     */
    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.core.impl.IProcessingRequests#getSentRequest()
     */
    @Override
    public IRequest getSentRequest() {
        return sentRequest;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.core.impl.IProcessingRequests#setSentRequest(edu.asu.diging.gilesecosystem.requests.IRequest)
     */
    @Override
    public void setSentRequest(IRequest sentRequest) {
        this.sentRequest = sentRequest;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.core.impl.IProcessingRequests#getCompletedRequest()
     */
    @Override
    public IRequest getCompletedRequest() {
        return completedRequest;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.core.impl.IProcessingRequests#setCompletedRequest(edu.asu.diging.gilesecosystem.requests.IRequest)
     */
    @Override
    public void setCompletedRequest(IRequest completedRequest) {
        this.completedRequest = completedRequest;
    }
    @Override
    public String getRequestId() {
        return requestId;
    }
    @Override
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    @Override
    public RequestStatus getRequestStatus() {
        return requestStatus;
    }
    @Override
    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }
}
