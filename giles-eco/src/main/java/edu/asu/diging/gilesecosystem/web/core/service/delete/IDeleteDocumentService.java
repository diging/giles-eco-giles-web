package edu.asu.diging.gilesecosystem.web.core.service.delete;

import edu.asu.diging.gilesecosystem.web.core.model.IDocument;

public interface IDeleteDocumentService {
    public abstract void deleteDocument(IDocument document);
    public abstract void deleteDocumentAfterStorageDeletion(IDocument document);
}
