package edu.asu.diging.gilesecosystem.web.core.service.processing.helpers;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.IRequestFactory;
import edu.asu.diging.gilesecosystem.requests.IStorageRequest;
import edu.asu.diging.gilesecosystem.requests.impl.StorageRequest;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.users.User;

@Component
public class RequestHelper {
    
    @Autowired
    private IRequestFactory<IStorageRequest, StorageRequest> requestFactory;
    
    @PostConstruct
    public void init() {
        requestFactory.config(StorageRequest.class);
    }

    /**
     * Creates a storage request object for the specified file and associated details.
     * @param file The file for which the storage request is created.
     * @param pathToFile The path to the file in the storage system.
     * @param downloadUrl The download URL for the file.
     * @param filetype The type of the file.
     * @param requestId The ID of the request.
     * @return An instance of {@link IStorageRequest} representing the storage request.
     * @throws GilesProcessingException If there is an error during the creation of the storage request.
     */
    public IStorageRequest createStorageRequest(IFile file, String pathToFile, String downloadUrl,
            FileType filetype, String requestId) throws GilesProcessingException {
        IStorageRequest request = null;
        try {
            request = requestFactory.createRequest(requestId, file.getUploadId());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new GilesProcessingException(e);
        }
        request.setDocumentId(file.getDocumentId());
        request.setFileId(file.getId());
        request.setDownloadPath(pathToFile);
        request.setDownloadUrl(downloadUrl);
        request.setFileType(filetype);
        request.setUploadDate(file.getUploadDate());
        request.setFilename(file.getFilename());
        request.setUsername(file.getUsernameForStorage());
        
        return request;
    }

    /**
    * Creates a storage request object for the specified file and associated details.
    * @param file The file for which the storage request is created.
    * @param pathToFile The path to the file in the storage system.
    * @param downloadUrl The download URL for the file.
    * @param filetype The type of the file.
    * @param requestId The ID of the request.
    * @param derivedFile Indicates if the file is a derived file thats processed by a non-core component.
    * @return An instance of {@link IStorageRequest} representing the storage request.
    * @throws GilesProcessingException If there is an error during the creation of the storage request.
    */
    public IStorageRequest createStorageRequest(IFile file, String pathToFile, String downloadUrl,
            FileType filetype, String requestId, boolean derivedFile) throws GilesProcessingException {
        IStorageRequest request = null;
        try {
            request = requestFactory.createRequest(requestId, file.getUploadId());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new GilesProcessingException(e);
        }
        request.setDocumentId(file.getDocumentId());
        request.setFileId(file.getId());
        request.setDownloadPath(pathToFile);
        request.setDownloadUrl(downloadUrl);
        request.setFileType(filetype);
        request.setUploadDate(file.getUploadDate());
        request.setFilename(file.getFilename());
        request.setUsername(file.getUsernameForStorage());
        request.setDerivedFile(derivedFile);
        return request;
    }
    
    public String getUsernameForStorage(User user) {
        return getUsernameForStorage(user.getProvider(), user.getUserIdOfProvider());
    }
    
    public String getUsernameForStorage(String provider, String userIdOfProvider) {
        return provider + "_" + userIdOfProvider;
    }
}
