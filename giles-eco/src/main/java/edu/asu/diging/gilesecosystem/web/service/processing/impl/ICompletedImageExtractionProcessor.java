package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import edu.asu.diging.gilesecosystem.requests.ICompletedImageExtractionRequest;

public interface ICompletedImageExtractionProcessor {

    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.processing.impl.ICompletedTextExtractionProcessor#processCompletedRequest(edu.asu.diging.gilesecosystem.requests.ICompletedTextExtractionRequest)
     */
    public abstract void processCompletedRequest(ICompletedImageExtractionRequest request);

}