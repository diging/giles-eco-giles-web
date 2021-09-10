package edu.asu.diging.gilesecosystem.web.core.model.impl;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.web.core.model.ITask;

@Entity
public class Task implements ITask {

    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE) private Integer id;
    private String taskHandlerId;
    private RequestStatus status;
    private String fileId;
    private String resultFileId;
    
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
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    @Override
    public String getResultFileId() {
        return resultFileId;
    }
    @Override
    public void setResultFileId(String resultFileId) {
        this.resultFileId = resultFileId;
    }
}
