package edu.asu.diging.gilesecosystem.web.core.service.core;

import java.util.List;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.model.IUpload;

public interface ITransactionalUploadService {

    String generateUploadId();

    IUpload getUpload(String id);

    IUpload getUploadByProgressId(String progressId);

    void saveUpload(IUpload upload) throws UnstorableObjectException;

    /**
     * Get specified page of upload query. If pageSize is -1, default page size is 
     * used. This method makes sure that only valid page numbers are used. If page 
     * is smaller than 1, it is set to 1 before querying the database. If page is 
     * greater than the max page count, it is set to the last page.
     * 
     *  @param username Username of the user that uploads belong to
     *  @param page number of page that should be retrieved
     *  @param pageSize number of results per page. If -1, then the default page size is used.
     */
    List<IUpload> getUploadsOfUser(String username, int page, int pageSize,
            String sortBy, int sortDirection);

    int getUploadsOfUserPageCount(String username);

    long getUploadsOfUserCount(String username);

    IUpload createUpload(String username, String uploadingApp, String uploadId, String uploadDate, String uploadProgressId);

    int getUploadsPageCount();

    List<IUpload> getUploads(int page, int pageSize, String sortBy, int sortDirection);

    long getUploadsCount();

}