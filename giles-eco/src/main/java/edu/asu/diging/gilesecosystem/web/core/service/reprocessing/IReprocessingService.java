package edu.asu.diging.gilesecosystem.web.core.service.reprocessing;

import edu.asu.diging.gilesecosystem.web.core.model.IDocument;

public interface IReprocessingService {
    /**
     * Reprocesses a document by marking its associated files as unprocessed, deleting previous processing requests,
     * and triggering the processing of the document's file using the process coordinator.
     * All the stages of processing is triggered again for the file which includes processing by core components like :
     * Nepomuk, Andromeda, Cepheus, Cassiopeia, etc
     * and non-core components like : Imogen, Carolus, Tardis, etc.
     * If a file is already processed the old storage id of the file is added to the oldFileVersionIds list and the new storage id is updated in the file.
     * @param document The document to be reprocessed.
     */
    public abstract void reprocessDocument(IDocument document);
}
