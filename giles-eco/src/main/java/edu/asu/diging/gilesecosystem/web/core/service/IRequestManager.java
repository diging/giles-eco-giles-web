package edu.asu.diging.gilesecosystem.web.core.service;

import java.util.concurrent.ExecutionException;

import edu.asu.diging.gilesecosystem.web.core.service.impl.ResendingResult;

public interface IRequestManager {

    public abstract void startResendingRequests();

    public ResendingResult getResendingResults() throws InterruptedException,
            ExecutionException;

}