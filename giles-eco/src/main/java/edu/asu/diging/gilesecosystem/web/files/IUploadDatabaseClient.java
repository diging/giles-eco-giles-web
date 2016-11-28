package edu.asu.diging.gilesecosystem.web.files;

import java.util.List;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.IDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.IUpload;

public interface IUploadDatabaseClient extends IDatabaseClient<IUpload> {
    
    public static final int ASCENDING = 1;
    public static final int DESCENDING = -1;

    public abstract IUpload getUpload(String id);

    public abstract List<IUpload> getUploadsForUser(String username);

    public abstract List<IUpload> getUploadsForUser(String username, int page,
            int pageSize, String sortBy, int sortDirection);

    public abstract IUpload saveUpload(IUpload upload) throws UnstorableObjectException;

}