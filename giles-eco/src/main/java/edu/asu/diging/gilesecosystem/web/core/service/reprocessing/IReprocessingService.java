package edu.asu.diging.gilesecosystem.web.core.service.reprocessing;

import edu.asu.diging.gilesecosystem.web.core.model.IDocument;

public interface IReprocessingService {
    /**
    * Initiates the reprocessing of the specified document.
    * @param document The document to be reprocessed.
    */
    public abstract void reprocessDocument(IDocument document);
}
