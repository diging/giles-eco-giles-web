package edu.asu.diging.gilesecosystem.web.service.processing;

import edu.asu.diging.gilesecosystem.requests.ICompletedOCRRequest;

public interface ICompletedOCRProcessor {

    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.processing.impl.ICompletedTextExtractionProcessor#processCompletedRequest(edu.asu.diging.gilesecosystem.requests.ICompletedTextExtractionRequest)
     */
    public abstract void processCompletedRequest(ICompletedOCRRequest request);

}