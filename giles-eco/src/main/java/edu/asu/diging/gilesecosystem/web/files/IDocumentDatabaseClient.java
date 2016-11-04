package edu.asu.diging.gilesecosystem.web.files;

import java.util.List;

import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.db4o.IDatabaseClient;
import edu.asu.diging.gilesecosystem.web.exceptions.UnstorableObjectException;

public interface IDocumentDatabaseClient extends IDatabaseClient<IDocument> {

    public abstract IDocument saveDocument(IDocument document) throws UnstorableObjectException;

    public abstract IDocument getDocumentById(String id);

    public abstract List<IDocument> getDocumentByUploadId(String uploadId);

    public abstract List<IDocument> getDocumentByExample(IDocument doc);

    public abstract List<IDocument> getDocumentsByUsername(String username);

}