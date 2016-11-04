package edu.asu.giles.service.requests.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.asu.giles.service.requests.IStorageRequest;
import edu.asu.giles.service.requests.FileType;

/**
 * A request to Kafka to store a file.
 * 
 * @author jdamerow
 *
 */
public class StorageRequest extends Request implements IStorageRequest {
    
    public final static String REQUEST_TYPE = "giles.request_type.storage";
    
    @JsonProperty
    private String pathToFile;
    
    @JsonProperty
    private String downloadUrl;
    
    @JsonProperty
    private FileType fileType;
    
    public StorageRequest() {
        this.setRequestType(REQUEST_TYPE);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.giles.service.requests.impl.IStorageRequest#getPathToFile()
     */
    @Override
    public String getPathToFile() {
        return pathToFile;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.service.requests.impl.IStorageRequest#setPathToFile(java.lang.String)
     */
    @Override
    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.service.requests.impl.IStorageRequest#getDownloadUrl()
     */
    @Override
    public String getDownloadUrl() {
        return downloadUrl;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.service.requests.impl.IStorageRequest#setDownloadUrl(java.lang.String)
     */
    @Override
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    @Override
    public FileType getFileType() {
        return fileType;
    }
    @Override
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }
    
}
