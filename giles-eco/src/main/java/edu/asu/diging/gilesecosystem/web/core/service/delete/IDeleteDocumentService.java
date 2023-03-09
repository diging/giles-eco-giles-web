package edu.asu.diging.gilesecosystem.web.core.service.delete;

import edu.asu.diging.gilesecosystem.web.core.model.IDocument;

public interface IDeleteDocumentService {
    /**
     * Sends storage deletion messages for all the files of the document to nepomuk via kafka
     * @param document
     *         The document to be deleted.
     */
    public abstract void deleteDocument(IDocument document);
    
    /**
     * Handles the deletion of the document and upload once nepomuk has deleted all the files.
     * @param document
     *         The document to be deleted.
     */
    public abstract void deleteDocumentAfterStorageDeletion(IDocument document);
}
