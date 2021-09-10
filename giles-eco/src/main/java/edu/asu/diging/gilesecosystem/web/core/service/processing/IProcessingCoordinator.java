package edu.asu.diging.gilesecosystem.web.core.service.processing;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;

public interface IProcessingCoordinator {

    public abstract RequestStatus processFile(IFile file, IProcessingInfo info)
            throws GilesProcessingException;

}