package edu.asu.diging.gilesecosystem.web.service.processing;

import edu.asu.diging.gilesecosystem.requests.ICompletedTextExtractionRequest;

public interface ICompletedTextExtractionProcessor {

    public abstract void processCompletedRequest(ICompletedTextExtractionRequest request);

}