package edu.asu.giles.service.requests;

public interface IStorageRequest extends IRequest {

    public abstract String getPathToFile();

    public abstract void setPathToFile(String pathToFile);

    public abstract String getDownloadUrl();

    public abstract void setDownloadUrl(String downloadUrl);

}