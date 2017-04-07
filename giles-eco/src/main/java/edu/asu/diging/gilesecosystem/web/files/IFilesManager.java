package edu.asu.diging.gilesecosystem.web.files;

import java.util.List;
import java.util.Map;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.DocumentType;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IUpload;
import edu.asu.diging.gilesecosystem.web.files.impl.StorageStatus;
import edu.asu.diging.gilesecosystem.web.users.User;

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
            User user, DocumentType docType, DocumentAccess access, String uploadProgressId);

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

    public abstract byte[] getFileContent(IFile file);

    public abstract List<IFile> getTextFilesOfDocument(IDocument doc);

    public abstract int getUploadsOfUserCount(String username);

    public abstract int getUploadsOfUserPageCount(String username);

    public abstract Map<String, Map<String, String>> getUploadedFilenames(String username);

    public abstract IFile getFileByRequestId(String requestId);
    
    /**
     * This method changes access type for document and all files attached
     * as pages to this document to provided new access type
     *
     * @param doc document for which access type is requested to change
     * @param docAccess new access type for document
     * @return true if the document access change was successfully; otherwise false.
     * @throws UnstorableObjectException for exception while saving updated document
     */
    public abstract boolean changeDocumentAccess(IDocument doc, DocumentAccess docAccess) throws UnstorableObjectException;

    public abstract IUpload getUploadByProgressId(String progressId);

}