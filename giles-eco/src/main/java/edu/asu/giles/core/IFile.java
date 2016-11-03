package edu.asu.giles.core;

import edu.asu.giles.db4o.IStorableObject;

public interface IFile extends IStorableObject {

    public abstract String getUploadId();

    public abstract void setUploadId(String uploadId);

    public abstract String getFilename();

    public abstract void setFilename(String filename);

    public abstract String getUsername();

    public abstract void setUsername(String username);

    public abstract String getId();

    public abstract void setId(String id);

    public abstract void setDocumentId(String zoteroDocumentId);

    public abstract String getDocumentId();

    public abstract void setUploadDate(String uploadDate);

    public abstract String getUploadDate();

    public abstract void setAccess(DocumentAccess access);

    public abstract DocumentAccess getAccess();

    public abstract void setSize(long size);

    public abstract long getSize();

    public abstract void setContentType(String contentType);

    public abstract String getContentType();

    public abstract void setFilepath(String filepath);

    public abstract String getFilepath();

    public abstract IFile clone();

    public abstract void setDerivedFrom(String derivedFrom);

    public abstract String getDerivedFrom();

}