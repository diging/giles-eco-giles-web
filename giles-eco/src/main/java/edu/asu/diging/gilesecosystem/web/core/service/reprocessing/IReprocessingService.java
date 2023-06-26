package edu.asu.diging.gilesecosystem.web.core.service.reprocessing;

import edu.asu.diging.gilesecosystem.web.core.model.IDocument;

public interface IReprocessingService {
    /**
     * Reprocesses a document by marking its associated files as unprocessed, deleting previous processing requests,
     * and triggering the processing of the document's file using the process coordinator.
     * All the stages of processing is triggered again for the file including core components like Nepomuk, Andromeda, Cepheus, Cassiopeia, 
     * and non-core components like Imogen, Carolus, Tardis, etc.
     * @param document The document to be reprocessed.
     */
    public abstract void reprocessDocument(IDocument document);
}
