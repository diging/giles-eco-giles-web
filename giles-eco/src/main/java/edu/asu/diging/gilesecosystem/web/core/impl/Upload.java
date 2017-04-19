package edu.asu.diging.gilesecosystem.web.core.impl;

import javax.jdo.annotations.Index;
import javax.persistence.Entity;
import javax.persistence.Id;

import edu.asu.diging.gilesecosystem.web.core.IUpload;

@Entity
public class Upload implements IUpload {

    @Id private String id;
    @Index private String username;
	private String createdDate;
	@Index private String uploadProgressId;
	
	public Upload() {}
	
	public Upload(String id) {
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

    @Override
    public String getUploadProgressId() {
        return uploadProgressId;
    }

    @Override
    public void setUploadProgressId(String uploadProgressId) {
        this.uploadProgressId = uploadProgressId;
    }
	
}
