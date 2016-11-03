package edu.asu.giles.files;

import java.util.List;
import java.util.Map;

import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.DocumentType;
import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.exceptions.UnstorableObjectException;
import edu.asu.giles.files.impl.StorageStatus;

public interface IFilesManager {

    /**
     * This method saves the given files to the database. It generates an id for
     * each file and an upload id that is the same for all files.
     * 
     * @param files
     *            The files to save.
     * @return The list of saved files with ids and upload id set.
     */
    public abstract List<StorageStatus> addFiles(Map<IFile, byte[]> files,
            String username, DocumentType docType, DocumentAccess access);

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
    public abstract List<IUpload> getUploadsOfUser(String username, int page, int pageSize, String sortBy, int sortDirection);

    public abstract IUpload getUpload(String id);

    public abstract List<IFile> getFilesByUploadId(String uploadId);

    public abstract List<IDocument> getDocumentsByUploadId(String uploadId);

    public abstract IDocument getDocument(String id);

    public abstract void saveDocument(IDocument document) throws UnstorableObjectException ;

    public abstract List<IFile> getFilesOfDocument(IDocument doc);

    public abstract IFile getFile(String id);

    public abstract void saveFile(IFile file) throws UnstorableObjectException;

    public abstract IFile getFileByPath(String path);

    public abstract String getFileUrl(IFile file);

    public abstract String getRelativePathOfFile(IFile file);

    public abstract byte[] getFileContent(IFile file);

    public abstract List<IFile> getTextFilesOfDocument(IDocument doc);

    public abstract int getUploadsOfUserCount(String username);

    public abstract int getUploadsOfUserPageCount(String username);

    public abstract Map<String, Map<String, String>> getUploadedFilenames(String username);

}