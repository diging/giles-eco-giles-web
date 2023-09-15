package edu.asu.diging.gilesecosystem.web.core.service.delete;

import edu.asu.diging.gilesecosystem.requests.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;

public interface IDeleteDocumentService {
    /**
     * Sends storage deletion messages for all the files of the document to nepomuk via kafka
     * @param document
     *         The document to be deleted.
     */
    void initiateDeletion(IDocument document) throws GilesProcessingException, MessageCreationException;
    
    /**
     * Handles the deletion of the document and upload once nepomuk has deleted all the files.
     * @param document
     *         The document to be deleted.
     */
    void completeDeletion(IDocument document);
}
