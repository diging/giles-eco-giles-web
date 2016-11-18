package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingInfo;
import edu.asu.diging.gilesecosystem.web.service.processing.ProcessingPhaseName;

@Service
public class CompletetionProcessingPhase extends ProcessingPhase<IProcessingInfo> {

    @Override
    public ProcessingPhaseName getPhaseName() {
        return ProcessingPhaseName.COMPLETE;
    }

    @Override
    protected IRequest createRequest(IFile file, IProcessingInfo info)
            throws GilesProcessingException {
        return null;
    }

    @Override
    protected String getTopic() {
        return null;
    }

    @Override
    protected ProcessingStatus getCompletedStatus() {
        return ProcessingStatus.COMPLETE;
    }

}
