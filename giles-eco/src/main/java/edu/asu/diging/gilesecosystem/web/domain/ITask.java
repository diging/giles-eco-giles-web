package edu.asu.diging.gilesecosystem.web.domain;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;

public interface ITask {

    public abstract String getTaskHandlerId();

    public abstract void setTaskHandlerId(String taskHandlerId);

    public abstract RequestStatus getStatus();

    public abstract void setStatus(RequestStatus status);

    public abstract void setFileId(String fileId);

    public abstract String getFileId();

}