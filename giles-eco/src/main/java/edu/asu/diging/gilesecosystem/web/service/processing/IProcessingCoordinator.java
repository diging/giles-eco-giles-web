package edu.asu.diging.gilesecosystem.web.service.processing;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.web.domain.IFile;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;

public interface IProcessingCoordinator {

    public abstract RequestStatus processFile(IFile file, IProcessingInfo info)
            throws GilesProcessingException;

}