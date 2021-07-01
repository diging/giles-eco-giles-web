package edu.asu.diging.gilesecosystem.web.core.service.processing.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IProcessingInfo;
import edu.asu.diging.gilesecosystem.web.core.service.processing.ProcessingPhaseName;

@Service
public class CompletetionProcessingPhase extends ProcessingPhase<IProcessingInfo> {

    final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    @Qualifier("tmpStorageManager") 
    private IFileStorageManager storageManager;
    
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

    @Override
    protected void postProcessing(IFile file) {
        storageManager.deleteFile(file.getUsernameForStorage(), file.getUploadId(), file.getDocumentId(), file.getFilename(), true);
    }

    @Override
    public Class<? extends IRequest> getSupportedRequestType() {
        // This is the final processor and doesn't support any requests.
        return null;
    }
}
