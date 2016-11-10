package edu.asu.diging.gilesecosystem.web.service.processing;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;

public interface ICompletedStorageRequestProcessor {

    public abstract void processCompletedRequest(ICompletedStorageRequest request);

}