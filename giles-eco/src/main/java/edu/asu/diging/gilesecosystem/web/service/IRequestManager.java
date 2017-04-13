package edu.asu.diging.gilesecosystem.web.service;

import java.util.concurrent.ExecutionException;

import edu.asu.diging.gilesecosystem.web.service.impl.ResendingResult;

public interface IRequestManager {

    public abstract void startResendingRequests();

    public ResendingResult getResendingResults() throws InterruptedException,
            ExecutionException;

}