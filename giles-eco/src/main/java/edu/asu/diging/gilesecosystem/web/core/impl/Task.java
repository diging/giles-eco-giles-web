package edu.asu.diging.gilesecosystem.web.core.impl;

import javax.persistence.Embeddable;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.web.core.ITask;

@Embeddable
public class Task implements ITask {

    private String taskHandlerId;
    private RequestStatus status;
    private String fileId;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.core.impl.ITask#getTaskHandlerId()
     */
    @Override
    public String getTaskHandlerId() {
        return taskHandlerId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.core.impl.ITask#setTaskHandlerId(java.lang.String)
     */
    @Override
    public void setTaskHandlerId(String taskHandlerId) {
        this.taskHandlerId = taskHandlerId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.core.impl.ITask#getStatus()
     */
    @Override
    public RequestStatus getStatus() {
        return status;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.core.impl.ITask#setStatus(edu.asu.diging.gilesecosystem.requests.RequestStatus)
     */
    @Override
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
    @Override
    public String getFileId() {
        return fileId;
    }
    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
