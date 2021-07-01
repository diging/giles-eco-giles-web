package edu.asu.diging.gilesecosystem.web.core.service.processing;

import java.util.concurrent.Future;

import edu.asu.diging.gilesecosystem.web.core.service.impl.ResendingResult;

public interface IRequestResender {

    public Future<ResendingResult> resendRequests();

}