package edu.asu.diging.gilesecosystem.web.core.model.impl;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import edu.asu.diging.gilesecosystem.web.core.model.IUpload;

@Entity
@Table(indexes={
        @Index(columnList="uploadProgressId", name="IDX_PROGRESS_ID"),
        @Index(columnList="username", name="IDX_USERNAME"),
        @Index(columnList="createdDate", name="IDX_CREATED_DATE")
})
public class Upload implements IUpload {

    @Id private String id;
    private String username;
	private String createdDate;
	private String uploadProgressId;
	
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
