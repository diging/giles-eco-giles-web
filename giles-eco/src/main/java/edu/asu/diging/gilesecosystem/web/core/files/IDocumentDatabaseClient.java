package edu.asu.diging.gilesecosystem.web.core.files;

import java.util.List;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.IDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;

public interface IDocumentDatabaseClient extends IDatabaseClient<IDocument> {

    public abstract IDocument saveDocument(IDocument document) throws UnstorableObjectException;

    public abstract IDocument getDocumentById(String id);

    public abstract List<IDocument> getDocumentByUploadId(String uploadId);

    public abstract List<IDocument> getDocumentsByUsername(String username);
    
    /**
     * Retrieves a document by its request ID.
     *
     * @param requestId The unique identifier of the request.
     * @return The document associated with the specified request ID, or null if no document is found.
     */
    public abstract IDocument getDocumentByRequestId(String requestId);
}
