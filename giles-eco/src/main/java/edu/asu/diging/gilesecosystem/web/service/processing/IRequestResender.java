package edu.asu.diging.gilesecosystem.web.service.processing;

import java.util.concurrent.Future;

import edu.asu.diging.gilesecosystem.web.service.impl.ResendingResult;

public interface IRequestResender {

    public Future<ResendingResult> resendRequests();

}