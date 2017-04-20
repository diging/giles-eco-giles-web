package edu.asu.diging.gilesecosystem.web.controllers.pages;

import java.util.List;

import edu.asu.diging.gilesecosystem.web.domain.IFile;
import edu.asu.diging.gilesecosystem.web.domain.IUpload;

public class UploadPageBean implements IUpload {

	private String id;
	private String username;
	private String createdDate;
	private int nrOfDocuments;
	private List<IFile> uploadedFiles;
	private String uploadProgressId;
	private Badge status;
	
	public UploadPageBean() {}
	
	public UploadPageBean(String id) {
		super();
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IUpload#getId()
	 */
	@Override
	public String getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IUpload#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IUpload#getUsername()
	 */
	@Override
	public String getUsername() {
		return username;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IUpload#setUsername(java.lang.String)
	 */
	@Override
	public void setUsername(String username) {
		this.username = username;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IUpload#getCreatedDate()
	 */
	@Override
	public String getCreatedDate() {
		return createdDate;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IUpload#setCreatedDate(java.util.Date)
	 */
	@Override
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

    public int getNrOfDocuments() {
        return nrOfDocuments;
    }

    public void setNrOfDocuments(int nrOfDocuments) {
        this.nrOfDocuments = nrOfDocuments;
    }

    public List<IFile> getUploadedFiles() {
        return uploadedFiles;
    }

    public void setUploadedFiles(List<IFile> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    public void setUploadProgressId(String uploadProgressId) {
        this.uploadProgressId = uploadProgressId;
    }

    public String getUploadProgressId() {
        return uploadProgressId;
    }

    public Badge getStatus() {
        return status;
    }

    public void setStatus(Badge status) {
        this.status = status;
    }
	
}
