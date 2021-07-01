package edu.asu.diging.gilesecosystem.web.core.service.processing;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;

public interface IProcessingPhase<T extends IProcessingInfo> {

    RequestStatus process(IFile file, T info) throws GilesProcessingException;
    
    ProcessingPhaseName getPhaseName();
}
