package edu.asu.diging.gilesecosystem.web.service.processing;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;

public interface IProcessingPhase<T extends IProcessingInfo> {

    RequestStatus process(IFile file, T info) throws GilesProcessingException;
    
    ProcessingPhaseName getPhaseName();
}
