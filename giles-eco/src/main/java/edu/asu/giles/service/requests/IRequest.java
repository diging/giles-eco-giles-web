package edu.asu.giles.service.requests;


public interface IRequest {

    public abstract String getUploadId();

    public abstract void setUploadId(String uploadId);

    public abstract RequestStatus getStatus();

    public abstract void setStatus(RequestStatus status);

    public abstract void setDocumentId(String documentId);

    public abstract String getDocumentId();

}