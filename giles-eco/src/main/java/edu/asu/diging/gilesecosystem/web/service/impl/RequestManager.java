package edu.asu.diging.gilesecosystem.web.service.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.web.service.IRequestManager;
import edu.asu.diging.gilesecosystem.web.service.processing.IRequestResender;

@Service
public class RequestManager implements IRequestManager {

    @Autowired
    private IRequestResender resender;
    
    private Future<ResendingResult> resendingResult;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.cassiopeia.core.service.impl.IRequestManager#startResendingRequests()
     */
    @Override
    public void startResendingRequests() {
        resendingResult = resender.resendRequests();
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.cassiopeia.core.service.impl.IRequestManager#getResendingResults()
     */
    @Override
    public ResendingResult getResendingResults() throws InterruptedException, ExecutionException {
        if (resendingResult == null) {
            return new ResendingResult(0, null);
        }
        if (resendingResult.isDone()) {
            return resendingResult.get();
        }
        
        return null;
    }
}
