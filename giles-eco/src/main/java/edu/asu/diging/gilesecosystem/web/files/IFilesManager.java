package edu.asu.diging.gilesecosystem.web.files;

import java.util.List;
import java.util.Map;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.domain.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.domain.DocumentType;
import edu.asu.diging.gilesecosystem.web.domain.IDocument;
import edu.asu.diging.gilesecosystem.web.domain.IFile;
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

    public abstract List<IDocument> getDocumentsByUploadId(String uploadId);
  
    public abstract List<IFile> getFilesOfDocument(IDocument doc);

    public abstract String getFileUrl(IFile file);

    public abstract byte[] getFileContent(IFile file);

    public abstract List<IFile> getTextFilesOfDocument(IDocument doc);

    public abstract Map<String, Map<String, String>> getUploadedFilenames(String username);

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

}