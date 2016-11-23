package edu.asu.diging.gilesecosystem.web.core;

import edu.asu.diging.gilesecosystem.util.store.IStorableObject;


public interface IUpload extends IStorableObject {

    public abstract String getId();

    public abstract void setId(String id);

    public abstract String getUsername();

    public abstract void setUsername(String username);

    public abstract String getCreatedDate();

    public abstract void setCreatedDate(String createdDate);

}