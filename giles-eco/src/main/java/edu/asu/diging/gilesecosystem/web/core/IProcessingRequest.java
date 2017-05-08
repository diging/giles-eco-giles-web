package edu.asu.diging.gilesecosystem.web.core;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.util.store.IStorableObject;

/**
 * @deprecated
 *      Use {@link edu.asu.diging.gilesecosystem.web.domain.IProcessingRequest} instead. This
 *      interface is only kept for migration purposes.
 * @author jdamerow
 *
 */
@Deprecated
public interface IProcessingRequest extends IStorableObject {

    public abstract String getId();

    public abstract void setId(String id);

    public abstract String getDocumentId();

    public abstract void setDocumentId(String documentId);

    public abstract String getFileId();

    public abstract void setFileId(String fileId);

    public abstract IRequest getSentRequest();

    public abstract void setSentRequest(IRequest sentRequest);

    public abstract IRequest getCompletedRequest();

    public abstract void setCompletedRequest(IRequest completedRequest);

    public abstract void setRequestId(String requestId);

    public abstract String getRequestId();

    public abstract void setRequestStatus(RequestStatus requestStatus);

    public abstract RequestStatus getRequestStatus();

}