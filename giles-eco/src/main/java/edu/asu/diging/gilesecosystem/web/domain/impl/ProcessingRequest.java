package edu.asu.diging.gilesecosystem.web.domain.impl;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.impl.Request;
import edu.asu.diging.gilesecosystem.web.domain.IProcessingRequest;

/**
 * Storage class for request objects.
 * 
 * @author jdamerow
 *
 */
@Entity
@Table(indexes={
        @Index(columnList="documentId", name="IDX_DOCUMENT_ID"),
        @Index(columnList="fileId", name="IDX_FILE_ID")
})
public class ProcessingRequest implements IProcessingRequest {

    @Id private String id;
    private String documentId;
    private String fileId;
    private String requestId;
    private RequestStatus requestStatus;
    @OneToOne(targetEntity=Request.class) @Cascade({CascadeType.ALL}) private IRequest sentRequest;
    @OneToOne(targetEntity=Request.class) @Cascade({CascadeType.ALL})  private IRequest completedRequest;
    
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
