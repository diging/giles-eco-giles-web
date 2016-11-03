package edu.asu.giles.core.impl;

import edu.asu.giles.core.IUpload;

public class Upload implements IUpload {

	private String id;
	private String username;
	private String createdDate;
	
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
	
}
