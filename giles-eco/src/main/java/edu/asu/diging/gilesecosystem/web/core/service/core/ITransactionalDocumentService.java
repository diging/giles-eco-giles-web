package edu.asu.diging.gilesecosystem.web.core.service.core;

import java.util.List;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentType;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;

public interface ITransactionalDocumentService {

    void saveDocument(IDocument document) throws UnstorableObjectException;

    String generateDocumentId();

    List<IDocument> getDocumentsByUploadId(String uploadId);

    List<IDocument> getDocumentsByUsername(String username);

    IDocument getDocument(String id);

    IDocument createDocument(String uploadId, String uploadDate, DocumentAccess access, DocumentType docType,
            String username);

    /**
     * Delete a document given the document ID.
     * @param documentId 
     *         ID of the document to be deleted
     */
    public abstract void deleteDocument(String documentId);

}
