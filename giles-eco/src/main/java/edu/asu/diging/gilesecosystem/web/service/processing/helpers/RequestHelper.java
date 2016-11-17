package edu.asu.diging.gilesecosystem.web.service.processing.helpers;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.IRequestFactory;
import edu.asu.diging.gilesecosystem.requests.IStorageRequest;
import edu.asu.diging.gilesecosystem.requests.impl.StorageRequest;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.users.User;

@Component
public class RequestHelper {
    
    @Autowired
    private IRequestFactory<IStorageRequest, StorageRequest> requestFactory;
    
    @PostConstruct
    public void init() {
        requestFactory.config(StorageRequest.class);
    }

    public IStorageRequest createStorageRequest(IFile file, String pathToFile, String downloadUrl,
            FileType filetype) throws GilesProcessingException {
        IStorageRequest request = null;
        try {
            request = requestFactory.createRequest(file.getUploadId());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new GilesProcessingException(e);
        }
        
        request.setRequestId(file.getRequestId());
        request.setDocumentId(file.getDocumentId());
        request.setPathToFile(pathToFile);
        request.setDownloadUrl(downloadUrl);
        request.setFileType(filetype);
        request.setUploadDate(file.getUploadDate());
        request.setFilename(file.getFilename());
        request.setUsername(file.getUsernameForStorage());
        
        return request;
    }
    
    public String getUsernameForStorage(User user) {
        return getUsernameForStorage(user.getProvider(), user.getUserIdOfProvider());
    }
    
    public String getUsernameForStorage(String provider, String userIdOfProvider) {
        return provider + "_" + userIdOfProvider;
    }
}
