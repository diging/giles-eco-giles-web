package edu.asu.diging.gilesecosystem.web.files;

import java.util.List;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.IDatabaseClient;
import edu.asu.diging.gilesecosystem.web.domain.IDocument;

public interface IDocumentDatabaseClient extends IDatabaseClient<IDocument> {

    public abstract IDocument saveDocument(IDocument document) throws UnstorableObjectException;

    public abstract IDocument getDocumentById(String id);

    public abstract List<IDocument> getDocumentByUploadId(String uploadId);

    public abstract List<IDocument> getDocumentsByUsername(String username);

}