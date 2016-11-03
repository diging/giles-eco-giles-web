package edu.asu.giles.files;

import java.util.List;

import edu.asu.giles.core.IUpload;
import edu.asu.giles.db4o.IDatabaseClient;

public interface IUploadDatabaseClient extends IDatabaseClient<IUpload> {
    
    public static final int ASCENDING = 1;
    public static final int DESCENDING = -1;

    public abstract IUpload getUpload(String id);

    public abstract List<IUpload> getUploadsForUser(String username);

    public abstract List<IUpload> getUploadsForUser(String username, int page,
            int pageSize, String sortBy, int sortDirection);

}