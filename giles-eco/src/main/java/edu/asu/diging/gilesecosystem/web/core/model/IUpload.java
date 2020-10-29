package edu.asu.diging.gilesecosystem.web.core.model;

import edu.asu.diging.gilesecosystem.util.store.IStorableObject;


public interface IUpload extends IStorableObject {

    public abstract String getId();

    public abstract void setId(String id);

    public abstract String getUsername();

    public abstract void setUsername(String username);

    public abstract String getCreatedDate();

    public abstract void setCreatedDate(String createdDate);

    public abstract void setUploadProgressId(String uploadProgressId);

    public abstract String getUploadProgressId();

}